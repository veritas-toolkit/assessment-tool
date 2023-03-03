package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class TemplateQuestionnaireDao {
    @Autowired
    private TemplateQuestionMapper questionMapper;

    @Autowired
    private TemplateQuestionnaireMapper questionnaireMapper;

    public void save(TemplateQuestionnaire templateQuestionnaire) {
        questionnaireMapper.insert(templateQuestionnaire);
        questionMapper.saveAll(templateQuestionnaire.getMainQuestionList());
    }

    public void updateBasicInfo(TemplateQuestionnaire templateQuestionnaire) {
        int result = questionnaireMapper.updateBasicInfo(templateQuestionnaire);
        if (result == 0) {
            log.warn("update template questionnaire[{}] failed.", templateQuestionnaire.getId());
        }
    }

    public List<TemplateQuestionnaire> findAll() {
        List<TemplateQuestionnaire> list = questionnaireMapper.findAll();
        for (TemplateQuestionnaire questionnaire : list) {
            List<TemplateQuestion> questionList = questionMapper.findByTemplateId(questionnaire.getId());
            questionnaire.addAllQuestionList(questionList);
        }
        return list;
    }

    public Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword,
                                                                Integer businessScenario, int page, int pageSize) {
        return questionnaireMapper.findTemplatePageable(prefix, keyword, businessScenario, page, pageSize);
    }

    public List<TemplateQuestionnaire> findAllWithoutQuestion() {
        return questionnaireMapper.findAll();
    }

    public TemplateQuestionnaire findById(int templateId) {
        TemplateQuestionnaire questionnaire = questionnaireMapper.selectById(templateId);
        if (questionnaire == null) {
            return null;
        }
        List<TemplateQuestion> questionList = questionMapper.findByTemplateId(questionnaire.getId());
        questionnaire.addAllQuestionList(questionList);
        return questionnaire;
    }

    public List<TemplateQuestionnaire> findByBusinessScenario(BusinessScenarioEnum businessScenarioEnum) {
        return questionnaireMapper.findByBusinessScenario(businessScenarioEnum);
    }

    public void delete(Integer templateId) {
        questionnaireMapper.deleteById(templateId);
        LambdaQueryWrapper<TemplateQuestion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TemplateQuestion::getTemplateId, templateId);
        questionMapper.delete(wrapper);
    }

    // update question
    public int updateQuestionContent(TemplateQuestionnaire questionnaire, TemplateQuestion question) {
        questionnaireMapper.updateEditInfo(questionnaire);
        int result = questionMapper.updateContent(question);
        if (result == 0) {
            log.warn("update content failed, [{}]", question);
        }
        return result;
    }

    public int addMainQuestion(TemplateQuestionnaire questionnaire, TemplateQuestion toAdd) {
        questionnaireMapper.updateEditInfo(questionnaire);
        int result = questionMapper.saveAll(Collections.singletonList(toAdd));
        questionMapper.updateStructureInfo(questionnaire.findMainQuestionListByPrinciple(toAdd.getPrinciple()));
        return result;
    }

    public int addSubQuestion(TemplateQuestionnaire questionnaire,
                              TemplateQuestion mainQuestion,
                              TemplateQuestion toAddSub) {
        questionnaireMapper.updateEditInfo(questionnaire);
        int result = questionMapper.insert(toAddSub);
        questionMapper.updateStructureInfo(mainQuestion);
        return result;
    }

    public int deleteMainQuestion(TemplateQuestionnaire templateQuestionnaire, TemplateQuestion toDelete) {
        questionnaireMapper.updateEditInfo(templateQuestionnaire);
        int result = questionMapper.deleteById(toDelete.getId());
        for (TemplateQuestion sub : toDelete.getSubList()) {
            result += questionMapper.deleteById(sub.getId());
        }
        questionMapper.updateStructureInfo(templateQuestionnaire.allQuestionList());
        return result;
    }

    public int deleteSubQuestion(TemplateQuestionnaire templateQuestionnaire, Integer questionId, Integer subQuestionId) {
        questionnaireMapper.updateEditInfo(templateQuestionnaire);
        int result = questionMapper.deleteById(subQuestionId);
        TemplateQuestion main = templateQuestionnaire.findQuestion(questionId);
        questionMapper.updateStructureInfo(main.getSubList());
        return result;
    }


}
