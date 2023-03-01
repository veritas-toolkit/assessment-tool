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
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaireJson;
import org.veritas.assessment.biz.mapper.questionnaire.TemplateQuestionnaireDao;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
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

        for (BusinessScenarioEnum businessScenarioEnum : BusinessScenarioEnum.values()) {
            List<TemplateQuestionnaire> exist = templateQuestionnaireDao.findByBusinessScenario(businessScenarioEnum);
            if ( exist != null && ! exist.isEmpty()) {
                continue;
            }
            TemplateQuestionnaire templateQuestionnaire = questionnaireJson.toTemplateQuestionnaire();
            templateQuestionnaire.setCreatorUserId(1);
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
        newOne.setCreatedTime(new Date());
        newOne.setCreatorUserId(operator.getId());

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
        if (!templateQuestionnaire.canBeEditOrDeleted()) {
            throw new IllegalRequestException("Cannot edit this questionnaire template.");
        }
        templateQuestionnaire.setName(name);
        templateQuestionnaire.setDescription(description);
        templateQuestionnaireDao.updateBasicInfo(templateQuestionnaire);
        return templateQuestionnaire;
    }


    // update question content

    // delete question(main or sub)


}
