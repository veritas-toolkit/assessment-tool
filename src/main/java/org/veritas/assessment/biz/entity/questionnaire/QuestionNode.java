package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.exception.HasBeenModifiedException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class QuestionNode implements Comparable<QuestionNode> {
    /**
     * questionnaire version id.
     */
    private Long questionnaireVid;

    /**
     * belongs to the project
     */
    private Integer projectId;

    /**
     * refer to the question-meta.
     */
    private Long questionId;

    /**
     * question version id
     */
    private Long questionVid;

    /**
     * If current question is a main-question then equal to {@link #questionId}.
     */
    private Long mainQuestionId;

    private Principle principle;

    /**
     * Step serial, range from 0 to 4 in database.
     */
    private AssessmentStep step;

    /**
     * Serial no of principle.
     * <br/>
     * Example: For the first question of Fairness principle <b>F1</b>,
     * this serialOfPrinciple will be set as <b>1</b>.
     */
    private Integer serialOfPrinciple;

    /**
     * Sub-question serial of main question.
     * If current question is a main question then set as 0.
     */
    private Integer subSerial;

    @TableField(exist = false)
    private QuestionMeta meta;

    @TableField(exist = false)
    private QuestionVersion questionVersion;

    @TableField(exist = false)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<QuestionNode> subList;

    public List<QuestionNode> getSubList() {
        if (!this.isMain()) {
            return Collections.emptyList();
        }
        if (subList == null) {
            return Collections.emptyList();
        }
        return subList;
    }

    public void setSubList(List<QuestionNode> subList) {
        if (subList == null || subList.isEmpty()) {
            this.subList = Collections.emptyList();
        } else {
            this.subList = subList.stream()
                    .filter(e -> Objects.equals(this.getQuestionnaireVid(), e.getQuestionnaireVid()))
                    .filter(e -> Objects.equals(this.getMainQuestionId(), e.getMainQuestionId()))
                    .filter(e -> !e.isMain())
                    .sorted()
                    .collect(Collectors.toList());
        }
    }

    // example: F1, G2 ...
    public String serial() {
        return this.principle.getShortName() + this.getSerialOfPrinciple();
    }

    public void configureQuestionnaireVid(Long _questionnaireVid) {
        this.questionnaireVid = _questionnaireVid;
        this.getSubList().forEach(s -> s.configureQuestionnaireVid(_questionnaireVid));
    }

    public void configureSerialOfPrinciple(int _serialOfPrinciple) {
        this.serialOfPrinciple = _serialOfPrinciple;
        this.getSubList().forEach(s -> s.configureSerialOfPrinciple(_serialOfPrinciple));
    }

    public void initAddStartQuestionnaireVid(Long questionnaireVid) {
        this.meta.setAddStartQuestionnaireVid(questionnaireVid);
        this.getSubList().forEach(s -> s.getMeta().setAddStartQuestionnaireVid(questionnaireVid));
    }

    public void configureDeleteStartQuestionnaireVid(Long questionnaireVid) {
        this.meta.setDeleteStartQuestionnaireVid(questionnaireVid);
        this.getSubList().forEach(s -> s.getMeta().setDeleteStartQuestionnaireVid(questionnaireVid));
    }

    public List<QuestionNode> toPlaneNodeList() {
        List<QuestionNode> list = new ArrayList<>();
        list.add(this);
        list.addAll(this.getSubList());
        return Collections.unmodifiableList(list);
    }

    public Map<Long, QuestionNode> toIdNodeMap() {
        Map<Long, QuestionNode> map = new LinkedHashMap<>();
        map.put(this.getQuestionId(), this);
        this.getSubList().forEach(s -> map.put(s.getQuestionId(), s));
        return map;
    }

    public List<QuestionMeta> toPlaneMetaList() {
        List<QuestionMeta> list = new ArrayList<>();
        list.add(this.getMeta());
        list.addAll(this.getSubList().stream().map(QuestionNode::getMeta).collect(Collectors.toList()));
        return Collections.unmodifiableList(list);
    }

    public List<QuestionVersion> toPlaneQuestionList() {
        List<QuestionVersion> list = new ArrayList<>();
        list.add(this.getQuestionVersion());
        list.addAll(this.getSubList().stream().map(QuestionNode::getQuestionVersion).collect(Collectors.toList()));
        return Collections.unmodifiableList(list);
    }

    @Override
    public int compareTo(QuestionNode o) {
        boolean sameProject = Objects.equals(this.getProjectId(), o.getProjectId());
        boolean sameQuestionnaireVersion = Objects.equals(this.getQuestionnaireVid(), o.getQuestionnaireVid());
        if (!sameProject || !sameQuestionnaireVersion) {
            throw new IllegalArgumentException();
        }
        boolean samePrinciple = this.getPrinciple() == o.getPrinciple();
        if (!samePrinciple) {
            return this.getPrinciple().compareTo(o.getPrinciple());
        }
        if (!Objects.equals(this.getSerialOfPrinciple(), o.getSerialOfPrinciple())) {
            return this.getSerialOfPrinciple() - o.getSerialOfPrinciple();
        } else {
            return this.getSubSerial() - o.getSubSerial();
        }
    }

    @JsonIgnore
    public boolean isMain() {
        if (this.getSubSerial() == null) {
            throw new IllegalStateException();
        }
        return this.getSubSerial() == 0;
    }

    @JsonIgnore
    public boolean isSub() {
        return !this.isMain();
    }

    public String questionContent() {
        if (questionVersion == null) {
            return "";
        } else {
            return questionVersion.getContent();
        }
    }

    public static QuestionNode createFromTemplate(TemplateQuestion templateQuestion) {
        QuestionNode node = new QuestionNode();
        node.setPrinciple(templateQuestion.getPrinciple());
        node.setStep(templateQuestion.getStep());
        node.setSerialOfPrinciple(templateQuestion.getSerialOfPrinciple());
        node.setSubSerial(templateQuestion.getSubSerial());

        QuestionMeta meta = new QuestionMeta(templateQuestion);
        node.setMeta(meta);

        QuestionVersion questionVersion = new QuestionVersion(templateQuestion);
        node.setQuestionVersion(questionVersion);
        if (templateQuestion.isMain()) {
            List<QuestionNode> subNodeList = templateQuestion.getSubList().stream()
                    .map(QuestionNode::createFromTemplate)
                    .collect(Collectors.toList());
            node.setSubList(subNodeList);
        }
        return node;
    }

    @Deprecated
    public static QuestionNode createFromOther(QuestionNode other) {
        QuestionNode node = new QuestionNode();
        node.setPrinciple(other.getPrinciple());
        node.setStep(other.getStep());
        node.setSerialOfPrinciple(other.getSerialOfPrinciple());
        node.setSubSerial(other.getSubSerial());

        QuestionMeta meta = new QuestionMeta(other.getMeta());
        node.setMeta(meta);

        QuestionVersion questionVersion = new QuestionVersion(other.getQuestionVersion());
        node.setQuestionVersion(questionVersion);

        if (other.isMain()) {
            List<QuestionNode> subList = other.getSubList().stream()
                    .map(QuestionNode::createFromOther)
                    .collect(Collectors.toList());
            node.setSubList(subList);
        }
        return node;
    }

    public void initGenericPropertiesWithSubs(int creatorUserId, int projectId, long questionnaireVid,
                                              Date createdTime) {
        List<QuestionNode> nodeList = this.toPlaneNodeList();
        List<QuestionVersion> questionVersionList = this.toPlaneQuestionList();
        List<QuestionMeta> metaList = this.toPlaneMetaList();

        questionVersionList.forEach(e -> e.setContentEditUserId(creatorUserId));

        nodeList.forEach(e -> e.setProjectId(projectId));
        questionVersionList.forEach(e -> e.setProjectId(projectId));
        metaList.forEach(e -> e.setProjectId(projectId));

        nodeList.forEach(e -> e.setQuestionnaireVid(questionnaireVid));
        metaList.forEach(e -> e.setAddStartQuestionnaireVid(questionnaireVid));

        questionVersionList.forEach(e -> e.setContentEditTime(createdTime));

    }

    public void initQuestionId(Supplier<Long> idSupplier) {
        Long id = idSupplier.get();
        this.setQuestionId(id);
        this.getMeta().setId(id);
        this.getQuestionVersion().setQuestionId(id);
        if (this.isMain()) {
            this.initMainQuestionIdWithSubs(id);
            this.getSubList().forEach(sub -> {
                sub.initQuestionId(idSupplier);
            });
        }
    }

    public void initQuestionVid(Supplier<Long> questionVidSupplier) {
        Long questionVid = questionVidSupplier.get();
        this.setQuestionVid(questionVid);
        this.getMeta().setCurrentVid(questionVid);
        this.getQuestionVersion().setVid(questionVid);
        if (this.isMain()) {
            this.subList.forEach(sub -> sub.initQuestionVid(questionVidSupplier));
        }
    }

    private void initMainQuestionIdWithSubs(Long mainQuestionId) {
        this.mainQuestionId = mainQuestionId;
        this.getMeta().setMainQuestionId(mainQuestionId);
        this.getQuestionVersion().setMainQuestionId(mainQuestionId);
        if (this.isMain()) {
            this.getSubList().forEach(sub -> sub.initMainQuestionIdWithSubs(mainQuestionId));
        }
    }

    public void updateQuestionVersion(QuestionVersion questionVersion) {
        Objects.requireNonNull(questionVersion);
        Objects.requireNonNull(questionVersion.getVid());
        this.setQuestionVid(questionVersion.getVid());
        this.getMeta().setCurrentVid(questionVersion.getVid());
        this.setQuestionVersion(questionVersion);
    }

    public static QuestionNode findByQuestionId(List<QuestionNode> questionNodeList, long questionId) {
        if (questionNodeList == null || questionNodeList.isEmpty()) {
            return null;
        }
        return questionNodeList.stream()
                .filter(q -> q.getQuestionId() == questionId)
                .findFirst().orElse(null);

    }

    public boolean hasBeenEdited(QuestionNode otherNode) {
        Objects.requireNonNull(otherNode);
        if (!Objects.equals(this.questionId, otherNode.getQuestionId())) {
            throw new IllegalArgumentException();
        }
        if (!Objects.equals(this.getQuestionVid(), otherNode.getQuestionVid())) {
            return true;
        }
        if (this.isMain()) {
            Set<Long> allSubQuestionVidSet = new HashSet<>();
            allSubQuestionVidSet.addAll(this.subList.stream()
                    .map(QuestionNode::getQuestionVid)
                    .collect(Collectors.toSet()));
            allSubQuestionVidSet.addAll(otherNode.subList.stream()
                    .map(QuestionNode::getQuestionVid)
                    .collect(Collectors.toSet()));
            boolean thisDiff = allSubQuestionVidSet.size() != this.subList.size();
            boolean newDiff = allSubQuestionVidSet.size() != otherNode.subList.size();
            return thisDiff || newDiff;
        }
        return false;
    }

    public QuestionNode createNew() {
        QuestionNode questionNode = new QuestionNode();
        BeanUtils.copyProperties(this, questionNode);
        questionNode.setMeta(this.meta.clone());
        questionNode.setQuestionVersion(this.getQuestionVersion().clone());
        if (this.isMain()) {
            questionNode.subList = this.subList.stream()
                    .map(QuestionNode::createNew)
                    .collect(Collectors.toList());
        }
        return questionNode;
    }

    public static List<QuestionNode> createNewList(List<QuestionNode> list) {
        return list.stream()
                .map(QuestionNode::createNew)
                .collect(Collectors.toList());
    }

    public List<Long> subQuestionIdList() {
        return this.getSubList().stream().map(QuestionNode::getQuestionId).collect(Collectors.toList());
    }

    public void sortSubList(List<Long> subQuestionIdOrderList) {
        if (!this.isMain()) {
            throw new IllegalStateException();
        }
        if (this.getSubList().isEmpty()) {
            return;
        }
        if (this.getSubList().size() != subQuestionIdOrderList.size()) {
            throw new IllegalArgumentException();
        }
        Map<Long, QuestionNode> subMap = this.getSubList().stream()
                .collect(Collectors.toMap(QuestionNode::getQuestionId, s -> s));

        Map<Long, QuestionNode> newOrderedMap = new LinkedHashMap<>();
        int serial = 1;
        for (Long id : subQuestionIdOrderList) {
            QuestionNode node = subMap.get(id);
            if (node == null) {
                throw new HasBeenModifiedException("");
            }
            newOrderedMap.put(id, node);
        }
    }

    // check the question be edited.
    public boolean isQuestionContentSame(QuestionNode other) {
        if (!Objects.equals(this.getQuestionId(), other.getQuestionId())) {
            throw new IllegalArgumentException();
        }
        List<QuestionVersion> thisVersionList = this.toPlaneQuestionList();
        List<QuestionVersion> otherVersionList = other.toPlaneQuestionList();
        if (thisVersionList.size() != otherVersionList.size()) {
            return false;
        }
        for (int i = 0; i < thisVersionList.size(); ++i) {
            QuestionVersion thisVersion = thisVersionList.get(i);
            QuestionVersion otherVersion = otherVersionList.get(i);
            if (!Objects.equals(thisVersion.getQuestionId(), otherVersion.getQuestionId())) {
                return false;
            }
            if (!thisVersion.isQuestionContentSame(otherVersion)) {
                return false;
            }
        }
        return true;
    }

    public QuestionNode addSub(User operator, String subQuestionContent, Long beforeQuestionId, Date now,
                               Supplier<Long> idSupplier) {
        if (this.isSub()) {
            throw new IllegalStateException("Sub question cannot add sub question.");
        }

        // meta
        Long subQuestionId = idSupplier.get();
        Long subQuestionVid = idSupplier.get();
        QuestionMeta meta = new QuestionMeta();
        meta.setId(subQuestionId);
        meta.setCurrentVid(subQuestionVid);
        meta.setProjectId(this.getProjectId());
        meta.setMainQuestionId(this.getMainQuestionId());
        meta.setContentEditable(true);
        meta.setAnswerRequired(false);
        meta.setAddStartQuestionnaireVid(this.questionnaireVid);

        // version
        QuestionVersion q = new QuestionVersion();
        q.setVid(subQuestionVid);
        q.setQuestionId(this.getQuestionId());
        q.setMainQuestionId(this.getMainQuestionId());
        q.setContent(subQuestionContent);
        q.setContentEditable(true);
        q.setContentEditUserId(operator.getId());
        q.setContentEditTime(now);
        // node
        QuestionNode newNode = new QuestionNode();
        newNode.setQuestionnaireVid(this.questionnaireVid);
        newNode.setProjectId(this.getProjectId());
        newNode.setQuestionId(subQuestionId);
        newNode.setQuestionVid(subQuestionVid);
        newNode.setMainQuestionId(this.getMainQuestionId());
        newNode.setPrinciple(this.getPrinciple());
        newNode.setStep(this.getStep());
        newNode.setSerialOfPrinciple(this.getSerialOfPrinciple());

        newNode.setMeta(meta);
        newNode.setQuestionVersion(q);

        // add the new node to the list
        List<QuestionNode> newSubList = new ArrayList<>(this.getSubList().size() + 1);
        boolean added = false;
        int newSubSerial = 1;
        for (QuestionNode sub : this.getSubList()) {
            if (Objects.equals(sub.getQuestionId(), beforeQuestionId)) {
                newNode.setSubSerial(newSubSerial);
                ++newSubSerial;
                newSubList.add(newNode);
                added = true;
            }
            sub.setSubSerial(newSubSerial);
            ++newSubSerial;
            newSubList.add(sub);
        }
        if (!added) {
            newNode.setSubSerial(newSubSerial);
            newSubList.add(newNode);
        }
        this.setSubList(newSubList);


        return newNode;
    }

    public QuestionNode deleteSub(@NotNull Long subQuestionId) {
        if (this.isSub()) {
            throw new IllegalStateException();
        }
        List<QuestionNode> newSubList = new ArrayList<>(this.getSubList().size());
        QuestionNode deleted = null;
        int newSubSerial = 1;
        for (QuestionNode sub : this.getSubList()) {
            if (Objects.equals(sub.getQuestionId(), subQuestionId)) {
                deleted = sub;
            } else {
                sub.setSubSerial(newSubSerial);
                ++newSubSerial;
                newSubList.add(sub);
            }
        }
        if (deleted == null) {
            throw new NotFoundException("Not found the sub question in current main question.");
        } else {
            this.setSubList(newSubList);
        }
        return deleted;
    }

    public QuestionNode editSub(@NotNull User operator,
                                @NotNull Long subQuestionId,
                                String newContent,
                                @NotNull Date now,
                                @NotNull Supplier<Long> idSupplier) {
        if (this.isSub()) {
            throw new IllegalStateException();
        }
        QuestionNode subNode = this.getSubList().stream()
                .filter(s -> Objects.equals(s.getQuestionId(), subQuestionId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Not found the question."));

        Long subQuestionVid = idSupplier.get();
        QuestionVersion question = new QuestionVersion();
        question.setVid(subQuestionVid);
        question.setQuestionId(this.getQuestionId());
        question.setMainQuestionId(this.getMainQuestionId());
        question.setContent(newContent);
        question.setContentEditable(true);
        question.setContentEditUserId(operator.getId());
        question.setContentEditTime(now);

        subNode.setQuestionVersion(question);
        QuestionMeta subMeta = subNode.getMeta();
        subMeta.setCurrentVid(subQuestionVid);


        return subNode;
    }

    public void reorderSub(List<Long> subQuestionIdList) {
        if (this.isSub()) {
            throw new IllegalStateException();
        }
        if (this.getSubList().size() != subQuestionIdList.size()) {
            throw new HasBeenModifiedException("The question has been modified.");
        }
        Map<Long, QuestionNode> map = new LinkedHashMap<>();
        this.getSubList().forEach(sub -> map.put(sub.getQuestionId(), sub));
        List<QuestionNode> newSubList = new ArrayList<>(this.getSubList().size());
        subQuestionIdList.forEach(subId -> {
            if (map.containsKey(subId)) {
                newSubList.add(map.get(subId));
            } else {
                throw new HasBeenModifiedException("The question has been modified.");
            }
        });
        int _subSerial = 1;
        for (QuestionNode questionNode : newSubList) {
            questionNode.setSubSerial(_subSerial);
            ++_subSerial;
        }
        this.setSubList(newSubList);
    }

}
