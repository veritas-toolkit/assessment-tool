package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;
import java.util.stream.Collectors;

// TODO: 2023/2/8 cache
@Repository
@Slf4j
public class QuestionnaireDao {
    private final QuestionMetaMapper questionMetaMapper;
    private final QuestionnaireVersionMapper questionnaireMapper;
    private final QuestionVersionMapper questionVersionMapper;

    private final QuestionNodeMapper questionNodeMapper;



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

    // cache?
    public boolean updateQuestionVidForLock(long questionId, long oldVid, long newVid) {
        return questionMetaMapper.updateVersionId(questionId, oldVid, newVid);
    }

    // edit question
    public boolean createNewVersion(QuestionnaireVersion questionnaireVersion, QuestionVersion questionVersion) {
        int questionnaireCount = questionnaireMapper.insert(questionnaireVersion);
        if (questionnaireCount != 1) {
            return false;
        }
        List<QuestionNode> questionNodeList = questionnaireVersion.finAllQuestionNodeList();
        int nodeCount = questionNodeMapper.saveAll(questionNodeList);
        if (nodeCount != questionNodeList.size()) {
            return false;
        }
        int questionCount = questionVersionMapper.insert(questionVersion);
        if (questionCount != 1) {
            return false;
        }
        if (log.isDebugEnabled()) {
            log.debug("questionnaire insert[{}] record, question-node insert [{}] records, question insert [{}] record",
                    questionnaireCount, nodeCount, questionCount);
        }
        return true;
        // meta has been update, not need to update.
    }

    public Pageable<QuestionnaireVersion> findHistoryPageable(int projectId, int page, int pageSize) {
        LambdaQueryWrapper<QuestionnaireVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionnaireVersion::getProjectId, projectId);

        wrapper.orderByAsc(QuestionnaireVersion::getVid);
        Page<QuestionnaireVersion> p = new Page<>();
        p.setCurrent(page);
        p.setSize(pageSize);
        Page<QuestionnaireVersion> page1 = questionnaireMapper.selectPage(p, wrapper);
        return Pageable.convert(page1);
    }
}
