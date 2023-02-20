package org.veritas.assessment.biz.mapper.questionnaire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

@Repository(value = "TemplateQuestionnaireDao2")
public class TemplateQuestionnaireDao {
    @Autowired
    private TemplateQuestionMapper questionMapper;

    @Autowired
    private TemplateQuestionnaireMapper questionnaireMapper;

    public void save(TemplateQuestionnaire templateQuestionnaire) {
        questionnaireMapper.insert(templateQuestionnaire);
        questionMapper.saveAll(templateQuestionnaire.getMainQuestionList());
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

    // update basic info

    // update question


}
