package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@TableName(autoResultMap = true)
public class QuestionnaireVersion implements Comparable<QuestionnaireVersion> {
    /**
     * Version id
     */
    @TableId(type = IdType.INPUT)
    private Long vid;

    private Integer projectId;

    private Integer modelArtifactVid;

    private Integer creatorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    private String message;

    @TableField(exist = false)
    private List<QuestionNode> mainQuestionNodeList;

    @Override
    public int compareTo(QuestionnaireVersion o) {
        if (this.projectId == null) {
            throw new IllegalStateException();
        }
        return this.getVid().compareTo(o.vid);
    }


    public QuestionnaireVersion(int creatorUserId,
                                int projectId,
                                Date createdTime,
                                TemplateQuestionnaire templateQuestionnaire,
                                Supplier<Long> idGenerator) {
        this.mainQuestionNodeList = templateQuestionnaire.getMainQuestionList().stream()
                .map(QuestionNode::createFromTemplate)
                .collect(Collectors.toList());
        this.init(creatorUserId, projectId, createdTime, idGenerator);
    }


    public List<QuestionMeta> findAllQuestionMetaList() {
        if (mainQuestionNodeList == null || mainQuestionNodeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionMeta> list = new ArrayList<>();
        for (QuestionNode node : mainQuestionNodeList) {
            list.add(node.getMeta());
            for (QuestionNode subNode : node.getSubList()) {
                list.add(subNode.getMeta());
            }
        }
        return Collections.unmodifiableList(list);
    }

    public List<QuestionNode> finAllQuestionNodeList() {
        if (mainQuestionNodeList == null || mainQuestionNodeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionNode> list = new ArrayList<>();
        for (QuestionNode node : mainQuestionNodeList) {
            list.add(node);
            list.addAll(node.getSubList());
        }
        return Collections.unmodifiableList(list);
    }

    public List<QuestionVersion> finAllQuestionVersionList() {
        if (mainQuestionNodeList == null || mainQuestionNodeList.isEmpty()) {
            return Collections.emptyList();
        }
        List<QuestionVersion> list = new ArrayList<>();
        for (QuestionNode node : mainQuestionNodeList) {
            list.add(node.getQuestionVersion());
            for (QuestionNode subNode : node.getSubList()) {
                list.add(subNode.getQuestionVersion());
            }
        }
        return Collections.unmodifiableList(list);
    }

    public void fill(List<QuestionNode> allNodeList, List<QuestionMeta> allMetaList, List<QuestionVersion> allQuestions) {
        Map<Long, QuestionMeta> metaMap = allMetaList.stream()
                .collect(Collectors.toMap(QuestionMeta::getId, meta -> meta));
        Map<Long, QuestionVersion> vidToQuestionMap = allQuestions.stream()
                .collect(Collectors.toMap(QuestionVersion::getVid, q -> q));

        for (QuestionNode node : allNodeList) {
            node.setMeta(metaMap.get(node.getQuestionId()));
            node.setQuestionVersion(vidToQuestionMap.get(node.getQuestionVid()));
        }

        List<QuestionNode> mainNodeList = allNodeList.stream()
                .filter(QuestionNode::isMain)
                .sorted()
                .collect(Collectors.toList());
        List<QuestionNode> subNodeList = allNodeList.stream()
                .filter(QuestionNode::isSub)
                .sorted()
                .collect(Collectors.toList());

        mainNodeList.forEach(node -> {
            node.setSubList(subNodeList);
        });
        this.mainQuestionNodeList = mainNodeList;
    }

    private void init(int creatorUserId, int projectId, Date createdTime, Supplier<Long> idGenerator) {
        Long questionnaireVid = idGenerator.get();
        // user, created time, project
        this.setProjectId(projectId);
        this.setCreatorUserId(creatorUserId);
        this.setCreatedTime(createdTime);
        this.setMessage(String.format("Created by user[%s], at %s", creatorUserId, createdTime));

        // questionnaire id
        this.setVid(questionnaireVid);
        for (QuestionNode node : mainQuestionNodeList) {
            node.initGenericPropertiesWithSubs(creatorUserId, projectId, questionnaireVid, createdTime);
        }

        // question id
        for (QuestionNode node : mainQuestionNodeList) {
            node.initQuestionId(idGenerator);
        }
        // question version id
        for (QuestionNode node : mainQuestionNodeList) {
            node.initQuestionVid(idGenerator);
        }
    }

    public Map<Principle, Map<AssessmentStep, List<QuestionNode>>> structure() {
        Map<Principle, Map<AssessmentStep, List<QuestionNode>>> map = new LinkedHashMap<>();
        for (Principle principle : Principle.values()) {
            Map<AssessmentStep, List<QuestionNode>> stepNode = new LinkedHashMap<>();
            for (AssessmentStep step : AssessmentStep.values()) {
                List<QuestionNode> questionNodeList = this.mainQuestionNodeList.stream()
                        .filter(q -> principle == q.getPrinciple() && step == q.getStep())
                        .sorted().collect(Collectors.toList());
                stepNode.put(step, questionNodeList);
            }
            map.put(principle, Collections.unmodifiableMap(stepNode));
        }
        return Collections.unmodifiableMap(map);
    }

    public List<QuestionNode> find(Principle principle, AssessmentStep step) {
        Objects.requireNonNull(principle);
        Objects.requireNonNull(step);
        return this.mainQuestionNodeList.stream()
                .filter(q -> principle == q.getPrinciple() && step == q.getStep())
                .sorted().collect(Collectors.toList());

    }

    public QuestionNode findMainQuestionById(long questionId) {
        return mainQuestionNodeList.stream().filter(q -> q.getQuestionId() == questionId)
                .findFirst().orElse(null);
    }
}
