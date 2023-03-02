package org.veritas.assessment.biz.service.questionnaire;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import freemarker.template.Template;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaireJson;
import org.veritas.assessment.biz.mapper.questionnaire.TemplateQuestionnaireDao;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.exception.UpdateException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TemplateQuestionnaireService {
    @Setter
    @Autowired
    private TemplateQuestionnaireDao templateQuestionnaireDao;

    @Autowired
    FreemarkerTemplateService freemarkerTemplateService;
    @PostConstruct
    @Transactional
    public List<TemplateQuestionnaire> load() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        final String urlString = "questionnaire_template/default.json";
        TemplateQuestionnaireJson questionnaireJson;
        try {
            URL url = new ClassPathResource(urlString).getURL();
            questionnaireJson = objectMapper.readValue(url, TemplateQuestionnaireJson.class);
        } catch (IOException exception) {
            throw new RuntimeException("load json failed", exception);
        }
        List<TemplateQuestionnaire> list = new ArrayList<>();
        Date now = new Date();

        for (BusinessScenarioEnum businessScenarioEnum : BusinessScenarioEnum.values()) {
            List<TemplateQuestionnaire> exist = templateQuestionnaireDao.findByBusinessScenario(businessScenarioEnum);
            if ( exist != null && ! exist.isEmpty()) {
                continue;
            }
            TemplateQuestionnaire templateQuestionnaire = questionnaireJson.toTemplateQuestionnaire();
            templateQuestionnaire.setCreatedTime(now);
            templateQuestionnaire.setCreatorUserId(1); // admin
            templateQuestionnaire.setEditTime(now);
            templateQuestionnaire.setEditUserId(1); // admin
            templateQuestionnaire.setBusinessScenario(businessScenarioEnum);
            templateQuestionnaire.setType(QuestionnaireTemplateType.SYSTEM);
            templateQuestionnaire.setName("Default Template for " + businessScenarioEnum.getName());
            for (TemplateQuestion templateQuestion : templateQuestionnaire.allQuestionList()) {
                String defaultFilename = templateQuestion.defaultFreeMarkTemplate();
                Template template = freemarkerTemplateService.findTemplate(businessScenarioEnum, defaultFilename);
                if (template != null) {
                    templateQuestion.setAnswerTemplateFilename(defaultFilename);
                }
            }
            templateQuestionnaireDao.save(templateQuestionnaire);
            list.add(templateQuestionnaire);

        }
        return Collections.unmodifiableList(list);
    }

    // all list
    @Transactional(readOnly = true)
    public List<TemplateQuestionnaire> findAllTemplateBasic() {
        return templateQuestionnaireDao.findAllWithoutQuestion();
    }

    /**
     * Find the questionnaire template list by keyword and business scenario.
     * <br/>
     * The keyword will search in template's name and description.
     * <br/>
     * Both <code>keyword</code> and <code>businessScenario</code> can be null.
     * @param keyword
     * @param businessScenario
     * @return
     */
    @Transactional(readOnly = true)
    public List<TemplateQuestionnaire> findByKeywordAndBiz(String keyword, Integer businessScenario) {
        List<TemplateQuestionnaire> list = templateQuestionnaireDao.findAllWithoutQuestion();
        if (keyword != null) {
            list = list.stream()
                    .filter(q -> StringUtils.containsAnyIgnoreCase(q.getName(), keyword)
                            || StringUtils.containsAnyIgnoreCase(q.getDescription(), keyword))
                    .collect(Collectors.toList());
        }
        if (businessScenario != null) {
            list = list.stream().filter(q -> q.getBusinessScenario().getCode() == businessScenario)
                    .collect(Collectors.toList());
        }
        return list;
    }

    @Transactional(readOnly = true)
    public Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword, Integer businessScenario,
                                                                int page, int pageSize) {
        return templateQuestionnaireDao.findTemplatePageable(prefix, keyword, businessScenario, page, pageSize);
    }

    // find by template id
    @Transactional(readOnly = true)
    public TemplateQuestionnaire findByTemplateId(int templateId) {
        return templateQuestionnaireDao.findById(templateId);
    }

    // create new template from another
    @Transactional
    public TemplateQuestionnaire create(User operator, Integer basicTemplateId, String name, String description) {
        TemplateQuestionnaire old = templateQuestionnaireDao.findById(basicTemplateId);
        if (old == null) {
            throw new NotFoundException("");
        }
        TemplateQuestionnaire newOne = new TemplateQuestionnaire();
        newOne.setBusinessScenario(old.getBusinessScenario());
        newOne.setType(QuestionnaireTemplateType.USER_DEFINED);
        newOne.setName(name);
        newOne.setDescription(description);
        Date now = new Date();
        newOne.setCreatedTime(now);
        newOne.setCreatorUserId(operator.getId());
        newOne.setEditTime(now);
        newOne.setEditUserId(operator.getId());


        List<TemplateQuestion> templateQuestionList = new ArrayList<>();
        for (TemplateQuestion templateQuestion : old.getMainQuestionList()) {
            templateQuestionList.add(templateQuestion.create());
        }
        newOne.setMainQuestionList(templateQuestionList);



        templateQuestionnaireDao.save(newOne);
        return newOne;
    }

    // delete the template.
    @Transactional
    public void delete(@Valid @NotNull Integer templateId) {
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireDao.findById(templateId);
        if (templateQuestionnaire == null) {
            // has been deleted, return success.
            return;
        }
        if (templateQuestionnaire.getType() == QuestionnaireTemplateType.SYSTEM) {
            throw new IllegalRequestException("Cannot delete system questionnaire template.");
        }
        templateQuestionnaireDao.delete(templateId);
    }

    // update basic information.

    @Transactional
    public TemplateQuestionnaire updateBasicInfo(User operator, Integer templateId, String name, String description) {
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireDao.findById(templateId);
        if (templateQuestionnaire == null) {
            throw new NotFoundException("Not found the questionnaire template.");
        }
        if (templateQuestionnaire.cannotBeEditOrDeleted()) {
            throw new IllegalRequestException("Cannot edit this questionnaire template.");
        }
        templateQuestionnaire.setName(name);
        templateQuestionnaire.setDescription(description);
        templateQuestionnaireDao.updateBasicInfo(templateQuestionnaire);
        return templateQuestionnaire;
    }


    // update question content

    @Transactional
    public TemplateQuestion updateQuestionContent(User operator, Integer templateId, Integer questionId, String content) {
        TemplateQuestionnaire questionnaire = templateQuestionnaireDao.findById(templateId);
        if (questionnaire == null) {
            throw new NotFoundException("Not found the questionnaire template.");
        }
        if (questionnaire.cannotBeEditOrDeleted()) {
            throw new IllegalRequestException("Cannot edit this questionnaire template.");
        }
        TemplateQuestion question = questionnaire.findQuestion(questionId);
        if (question == null) {
            throw new NotFoundException("Not found the question.");
        }
        if (!question.isEditable()) {
            throw new IllegalRequestException("Cannot edit this question.");
        }
        question.setContent(content);
        question.setEditTime(new Date());
        question.setEditorUserId(operator.getId());
        int result = templateQuestionnaireDao.updateQuestionContent(questionnaire, question);
        if (result == 0) {
            throw new UpdateException("Update the question failed.");
        }
        questionnaire = templateQuestionnaireDao.findById(templateId);
        return questionnaire.findQuestion(questionId);
    }

    // delete question(main or sub)

    @Transactional
    public TemplateQuestionnaire deleteMainQuestion(User operator, Integer templateId, Integer questionId) {
        TemplateQuestionnaire questionnaire = findTemplateForEdit(templateId);
        TemplateQuestion question = findQuestionForEdit(questionnaire, questionId);
        questionnaire.deleteMainQuestion(questionId);
        questionnaire.setEditTime(new Date());
        questionnaire.setEditUserId(operator.getId());
        templateQuestionnaireDao.deleteMainQuestion(questionnaire, question);
        return templateQuestionnaireDao.findById(templateId);
    }

    @Transactional
    public TemplateQuestion deleteSubQuestion(User operator, Integer templateId, Integer questionId, Integer subQuestionId) {
        TemplateQuestionnaire questionnaire = findTemplateForEdit(templateId);
        int result = questionnaire.deleteSubQuestion(questionId, subQuestionId);
        if (result == 0) {
            log.warn("Not found the sub question. Return success.");
            return questionnaire.findQuestion(questionId);
        }
        questionnaire.setEditTime(new Date());
        questionnaire.setEditUserId(operator.getId());
        templateQuestionnaireDao.deleteSubQuestion(questionnaire, questionId, subQuestionId);
        TemplateQuestionnaire after = templateQuestionnaireDao.findById(templateId);
        return after.findQuestion(questionId);
    }

    @Transactional
    public TemplateQuestionnaire addMainQuestion(User operator,
                                                 Integer templateId,
                                                 Principle principle,
                                                 AssessmentStep step,
                                                 Integer serialOfPrinciple,
                                                 List<String> questionContentList) {
        TemplateQuestionnaire questionnaire = findTemplateForEdit(templateId);

        List<TemplateQuestion> list = new ArrayList<>(questionContentList.size());
        int subSeq = 0;
        Date now = new Date();
        for (String content : questionContentList) {
            TemplateQuestion question = new TemplateQuestion();
            question.setTemplateId(templateId);
            question.setSubSerial(subSeq);
            ++subSeq;
            question.setPrinciple(principle);
            question.setStep(step);
            question.setContent(content);
            question.setEditable(true);
            question.setEditTime(now);
            question.setEditorUserId(operator.getId());
            list.add(question);
        }
        TemplateQuestion main = list.get(0);
        if (questionContentList.size() > 1) {
            List<TemplateQuestion> subList = list.subList(1, list.size());
            main.setSubList(subList);
        }
        int count = questionnaire.findMainQuestionListByPrinciple(principle).size();
        if (serialOfPrinciple == null || serialOfPrinciple > count) {
            serialOfPrinciple = count+1;
        } else if (serialOfPrinciple <= 1) {
            serialOfPrinciple = 1;
        }
        main.setSerialOfPrinciple(serialOfPrinciple);
        questionnaire.addMainQuestion(main);
        questionnaire.setEditTime(new Date());
        questionnaire.setEditUserId(operator.getId());
        templateQuestionnaireDao.addMainQuestion(questionnaire, main);
        return templateQuestionnaireDao.findById(templateId);
    }
    private TemplateQuestionnaire findTemplateForEdit(Integer templateId) {
        TemplateQuestionnaire questionnaire = templateQuestionnaireDao.findById(templateId);
        if (questionnaire == null) {
            throw new NotFoundException("Not found the questionnaire template.");
        }
        if (questionnaire.cannotBeEditOrDeleted()) {
            throw new IllegalRequestException("Cannot edit this questionnaire template.");
        }
        return questionnaire;
    }
    private TemplateQuestion findQuestionForEdit(TemplateQuestionnaire questionnaire, Integer questionId) {
        if (questionnaire.cannotBeEditOrDeleted()) {
            throw new IllegalRequestException("Cannot edit this questionnaire template.");
        }
        TemplateQuestion question = questionnaire.findQuestion(questionId);
        if (question == null) {
            throw new NotFoundException("Not found the question.");
        }
        if (!question.isEditable()) {
            throw new IllegalRequestException("Cannot edit this question.");
        }
        return question;
    }



}
