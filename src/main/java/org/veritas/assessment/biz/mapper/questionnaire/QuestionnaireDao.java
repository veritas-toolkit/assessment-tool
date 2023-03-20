package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

// TODO: 2023/2/8 cache
@Repository
@Slf4j
public class QuestionnaireDao {
    @Autowired
    private QuestionMetaMapper questionMetaMapper;
    @Autowired
    private QuestionnaireVersionMapper questionnaireMapper;
    @Autowired
    private QuestionVersionMapper questionVersionMapper;

    @Autowired
    private QuestionNodeMapper questionNodeMapper;

    public void saveNewQuestionnaire(QuestionnaireVersion questionnaireVersion) {
        int result = questionnaireMapper.insert(questionnaireVersion);

        int metaCount = questionMetaMapper.saveAll(questionnaireVersion.findAllQuestionMetaList());
        int nodeCount = questionNodeMapper.saveAll(questionnaireVersion.finAllQuestionNodeList());
        int questionCount = questionVersionMapper.saveAll(questionnaireVersion.finAllQuestionVersionList());

        log.debug("insert record count: questionnaire[{}], meta[{}], node[{}], question[{}]",
                result, metaCount, nodeCount, questionCount);
        if (result != 1 || metaCount != nodeCount || metaCount != questionCount) {
            log.warn("questionnaire: {}", result);
            log.warn("question meta: {}", metaCount);
            log.warn("question node: {}", nodeCount);
            log.warn("question version: {}", questionCount);
        }
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

    public QuestionnaireVersion findByQuestionnaireVid(long vid) {
        QuestionnaireVersion questionnaire = questionnaireMapper.findByQuestionnaireVid(vid);
        if (questionnaire == null) {
            return null;
        }
        List<QuestionNode> nodeList = questionNodeMapper.findByQuestionnaireVid(questionnaire.getVid());
        Integer projectId = questionnaire.getProjectId();
        List<QuestionMeta> questionMetaList = questionMetaMapper.findByProjectId(projectId);
        List<Long> questionVidList = nodeList.stream().map(QuestionNode::getQuestionVid).collect(Collectors.toList());
        List<QuestionVersion> questionVersionList = questionVersionMapper.findByIdList(questionVidList);
        questionnaire.fill(nodeList, questionMetaList, questionVersionList);
        return questionnaire;
    }


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
    }

    public boolean addNewQuestion(QuestionnaireVersion questionnaireVersion, QuestionNode questionNode) {
        if (log.isTraceEnabled()) {
            log.trace("questionnaire: {}", questionnaireVersion);
            log.trace("question: {}", questionNode);
        }
        int questionnaireCount = questionnaireMapper.insert(questionnaireVersion);
        if (questionnaireCount <= 0) {
            log.error("insert questionnaire record failed.");
            return false;
        }
        // insert meta
        int result = questionMetaMapper.saveAll(questionNode.toPlaneMetaList());
        // insert version
        result += questionVersionMapper.saveAll(questionNode.toPlaneQuestionList());
        result += questionNodeMapper.saveAll(questionnaireVersion.finAllQuestionNodeList());
        return result > 0;
    }

    public Pageable<QuestionnaireVersion> findHistoryPageable(int projectId, boolean exportedOnly, boolean draftOnly,
                                                              int page, int pageSize) {
        LambdaQueryWrapper<QuestionnaireVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionnaireVersion::getProjectId, projectId);
        if (exportedOnly) {
            wrapper.eq(QuestionnaireVersion::getExported, true);
        } else if (draftOnly) {
            wrapper.eq(QuestionnaireVersion::getExported, false);
        }

        wrapper.orderByAsc(QuestionnaireVersion::getVid);
        Page<QuestionnaireVersion> p = new Page<>();
        p.setCurrent(page);
        p.setSize(pageSize);
        Page<QuestionnaireVersion> page1 = questionnaireMapper.selectPage(p, wrapper);
        return Pageable.convert(page1);
    }

    public boolean saveStructure(QuestionnaireVersion questionnaireVersion) {
        int questionnaireCount = questionnaireMapper.insert(questionnaireVersion);
        if (questionnaireCount <= 0) {
            log.error("insert questionnaire record failed.");
            return false;
        }
        List<QuestionNode> allNodeList = questionnaireVersion.finAllQuestionNodeList();
        int nodeCount = questionNodeMapper.saveAll(allNodeList);
        if (nodeCount != allNodeList.size()) {
            log.error("insert questionnaire record failed.");
            return false;
        }
        return true;
    }

    public int updateAsExported(long vid) {
        return questionnaireMapper.updateAsExported(vid);
    }

    public boolean deleteMainQuestion(QuestionnaireVersion questionnaireVersion, QuestionNode delete) {
        Objects.requireNonNull(questionnaireVersion);
        Objects.requireNonNull(delete);
        questionMetaMapper.deleteMain(questionnaireVersion.getVid(), delete.getQuestionId());
        return this.saveStructure(questionnaireVersion);
    }

    public boolean deleteSubQuestion(QuestionnaireVersion questionnaire,
                                     Long mainQuestionId,
                                     List<Long> subQuestionIdList) {
        Objects.requireNonNull(questionnaire);
        Objects.requireNonNull(mainQuestionId);
        int result = questionMetaMapper.logicallyDeleteSub(
                questionnaire.getProjectId(),
                mainQuestionId,
                subQuestionIdList,
                questionnaire.getVid());
        return result > 0;
    }

    public void save(QuestionnaireVersion questionnaire) {
        Objects.requireNonNull(questionnaire);

        // question node add action.
        // insert meta
        // insert version
        questionnaire.getAddActionNodeList().forEach(node -> {
            questionMetaMapper.insert(node.getMeta());
            questionVersionMapper.insert(node.getQuestionVersion());
        });

        // delete
        questionnaire.getDeleteActionNodeList().forEach(node -> {
            questionMetaMapper.logicallyDelete(
                    questionnaire.getProjectId(),
                    node.getQuestionId(),
                    node.getQuestionnaireVid());
        });

        // edite
        // update meta
        questionnaire.getModifyActionNodeList().forEach(node -> {
            questionMetaMapper.updateCurrentVersionId(node.getQuestionId(), node.getQuestionVid());
            questionVersionMapper.insert(node.getQuestionVersion());
        });

        // insert all node
        questionNodeMapper.saveAll(questionnaire.finAllQuestionNodeList());
        // insert questionnaire
        questionnaireMapper.insert(questionnaire);
    }
}
