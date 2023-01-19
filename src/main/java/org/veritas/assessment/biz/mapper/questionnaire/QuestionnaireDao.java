package org.veritas.assessment.biz.mapper.questionnaire;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersionStructure;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class QuestionnaireDao {
    private QuestionMetaMapper questionMetaMapper;
    private QuestionnaireVersionMapper questionnaireMapper;
    private QuestionnaireVersionStructureMapper structureMapper;
    private QuestionVersionMapper questionVersionMapper;

    @Autowired
    public QuestionnaireDao(QuestionMetaMapper questionMetaMapper,
                            QuestionnaireVersionMapper questionnaireVersionMapper,
                            QuestionnaireVersionStructureMapper structureMapper,
                            QuestionVersionMapper questionVersionMapper) {
        this.questionMetaMapper = questionMetaMapper;
        this.questionnaireMapper = questionnaireVersionMapper;
        this.structureMapper = structureMapper;
        this.questionVersionMapper = questionVersionMapper;
    }

    // 根据 project 加载最新的
    public QuestionnaireVersion findLatestQuestionnaire(int projectId) {
        QuestionnaireVersion questionnaire = questionnaireMapper.findLatest(projectId);
        if (questionnaire == null) {
            return null;
        }
        List<QuestionMeta> questionMetaList = questionMetaMapper.findByProjectId(projectId);
        List<QuestionnaireVersionStructure> structureList = structureMapper.findByVid(questionnaire.getVid());
        List<Integer> questionVidList = structureList.stream()
                .map(QuestionnaireVersionStructure::getQuestionVid)
                .collect(Collectors.toList());
        List<QuestionVersion> questionList = questionVersionMapper.findByIdList(questionVidList);

        questionnaire.addContent(structureList, questionMetaList, questionList);
        return questionnaire;
    }
    // 加载Questionnaire by vid

}
