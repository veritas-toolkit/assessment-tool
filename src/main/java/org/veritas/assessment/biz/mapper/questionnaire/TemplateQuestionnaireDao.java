package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

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
        LambdaUpdateWrapper<TemplateQuestionnaire> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(TemplateQuestionnaire::getId, templateQuestionnaire.getId());
        wrapper.set(TemplateQuestionnaire::getName, templateQuestionnaire.getName());
        wrapper.set(TemplateQuestionnaire::getDescription, templateQuestionnaire.getDescription());
        questionnaireMapper.update(null, wrapper);
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
        TemplateQuestionnaire q = questionnaireMapper.selectById(templateId);
        if (q == null) {
            return null;
        }
        List<TemplateQuestion> questionList = questionMapper.findByTemplateId(q.getId());
        q.addAllQuestionList(questionList);
        return q;
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
    public int updateQuestionContent(TemplateQuestion question) {
        int result = questionMapper.updateContent(question);
        if (result == 0) {
            log.warn("update content failed, [{}]", question);
        }
        return result;
    }


}
