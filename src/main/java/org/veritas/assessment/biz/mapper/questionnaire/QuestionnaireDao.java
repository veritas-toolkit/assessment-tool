package org.veritas.assessment.biz.mapper.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class QuestionnaireDao {
    private QuestionMetaMapper questionMetaMapper;
    private QuestionnaireVersionMapper questionnaireMapper;
    private QuestionVersionMapper questionVersionMapper;

    private QuestionNodeMapper questionNodeMapper;

    @Autowired
    public QuestionnaireDao(QuestionMetaMapper questionMetaMapper,
                            QuestionnaireVersionMapper questionnaireVersionMapper,
                            QuestionVersionMapper questionVersionMapper,
                            QuestionNodeMapper questionNodeMapper) {
        this.questionMetaMapper = questionMetaMapper;
        this.questionnaireMapper = questionnaireVersionMapper;
        this.questionVersionMapper = questionVersionMapper;
        this.questionNodeMapper = questionNodeMapper;
    }

    public void saveNewQuestionnaire(QuestionnaireVersion questionnaireVersion) {
        int result = questionnaireMapper.insert(questionnaireVersion);

        int metaCount = questionMetaMapper.saveAll(questionnaireVersion.findAllQuestionMetaList());
        int nodeCount = questionNodeMapper.saveAll(questionnaireVersion.finAllQuestionNodeList());
        int questionCount = questionVersionMapper.saveAll(questionnaireVersion.finAllQuestionVersionList());

        log.debug("insert record count: questionnaire[{}], meta[{}], node[{}], question[{}]",
                result, metaCount, nodeCount, questionCount);

        assert result == 1;
        assert metaCount == nodeCount;
        assert metaCount == questionCount;
    }

    // 根据 project 加载最新的
    public QuestionnaireVersion findLatestQuestionnaire(int projectId) {
        QuestionnaireVersion questionnaire = questionnaireMapper.findLatest(projectId);
        if (questionnaire == null) {
            return null;
        }
        List<QuestionNode> nodeList = questionNodeMapper.findByQuestionnaireVid(questionnaire.getVid());
        List<QuestionMeta> questionMetaList = questionMetaMapper.findByProjectId(projectId);
        List<Long> questionVidList = nodeList.stream().map(QuestionNode::getQuestionVid).collect(Collectors.toList());
        List<QuestionVersion> questionVersionList = questionVersionMapper.findByIdList(questionVidList);
        questionnaire.fill(nodeList, questionMetaList, questionVersionList);

        return questionnaire;
    }
    // 加载Questionnaire by vid

}
