package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.veritas.assessment.biz.action.AddSubQuestionAction;
import org.veritas.assessment.biz.action.DeleteSubQuestionAction;
import org.veritas.assessment.biz.action.EditAnswerAction;
import org.veritas.assessment.biz.action.EditMainQuestionAction;
import org.veritas.assessment.biz.action.EditSubQuestionAction;
import org.veritas.assessment.biz.action.ReorderSubQuestionAction;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.exception.HasBeenModifiedException;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.handler.TimestampHandler;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
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
@Slf4j
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

    private Boolean exported = DEFAULT_EXPORTED;

    @TableField(exist = false)
    private List<QuestionNode> mainQuestionNodeList;

    @TableField(exist = false)
    private List<QuestionNode> addActionNodeList = new ArrayList<>();
    @TableField(exist = false)
    private List<QuestionNode> deleteActionNodeList = new ArrayList<>();
    @TableField(exist = false)
    private List<QuestionNode> modifyActionNodeList = new ArrayList<>();

    private static boolean DEFAULT_EXPORTED = false;

    @Override
    public int compareTo(QuestionnaireVersion o) {
        if (this.projectId == null) {
            throw new IllegalStateException();
        }
        return this.getVid().compareTo(o.vid);
    }


    public List<QuestionNode> getMainQuestionNodeList() {
        return mainQuestionNodeList == null ? Collections.emptyList() : mainQuestionNodeList;
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

    public QuestionnaireVersion(int creatorUserId,
                                int projectId,
                                Date createdTime,
                                QuestionnaireVersion old,
                                Supplier<Long> idGenerator) {
        this.mainQuestionNodeList = old.getMainQuestionNodeList().stream()
                .map(QuestionNode::createFromOther)
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

    public List<QuestionNode> findMainQuestion(Principle principle) {
        Objects.requireNonNull(principle);
        return this.mainQuestionNodeList.stream()
                .filter(q -> principle == q.getPrinciple())
                .sorted().collect(Collectors.toList());
    }

    public QuestionNode findMainQuestionById(long questionId) {
        return mainQuestionNodeList.stream().filter(q -> q.getQuestionId() == questionId)
                .findFirst().orElse(null);
    }

    // serial example: F1
    public QuestionNode findMainQuestionBySerial(String serial) {
        return mainQuestionNodeList.stream().filter(node -> StringUtils.equals(node.serial(), serial))
                .findFirst().orElse(null);
    }

    public QuestionNode findNodeByQuestionId(long questionId) {
        List<QuestionNode> all = this.finAllQuestionNodeList();
        return all.stream()
                .filter(node -> questionId == node.getQuestionId())
                .findFirst()
                .orElse(null);
    }

    public void configureQuestionnaireVid(long newVid) {
        this.setVid(newVid);
        this.finAllQuestionNodeList().forEach(node -> node.setQuestionnaireVid(newVid));
    }

    // create a new version for questionnaire.
    public QuestionnaireVersion createNewVersion(User operator, Date now, Supplier<Long> idSupplier) {
        Objects.requireNonNull(operator);
        if (now == null) {
            now = new Date();
        }
        Long newVid = idSupplier.get();
        QuestionnaireVersion newQuestionnaire = new QuestionnaireVersion();
        newQuestionnaire.setProjectId(this.getProjectId());
        newQuestionnaire.setModelArtifactVid(this.getModelArtifactVid());
        newQuestionnaire.setCreatorUserId(operator.getId());
        newQuestionnaire.setCreatedTime(now);
        newQuestionnaire.setExported(false);
        newQuestionnaire.setMessage(null);

        newQuestionnaire.mainQuestionNodeList = QuestionNode.createNewList(this.mainQuestionNodeList);
        newQuestionnaire.configureQuestionnaireVid(newVid);
        return newQuestionnaire;
    }

    synchronized
    public void addMainQuestion(QuestionNode main) {
        main.setSerialOfPrinciple(Integer.MAX_VALUE);
        List<QuestionNode> list = new ArrayList<>(this.getMainQuestionNodeList().size());
        list.addAll(this.getMainQuestionNodeList());
        list.add(main);
        list = list.stream().sorted((a, b) -> {
            int result = a.getPrinciple().compareTo(b.getPrinciple());
            if (result == 0) {
                result = a.getStep().compareTo(b.getStep());
            }
            if (result == 0) {
                return a.getSerialOfPrinciple().compareTo(b.getSerialOfPrinciple());
            }
            return result;
        }).collect(Collectors.toList());
        Principle principle = main.getPrinciple();
        final int SERIAL_OF_PRINCIPLE_START = 1;
        int serial = SERIAL_OF_PRINCIPLE_START;
        for (QuestionNode questionNode : list) {
            if (principle == questionNode.getPrinciple()) {
                questionNode.configureSerialOfPrinciple(serial);
                ++serial;
            }
        }
        main.configureQuestionnaireVid(this.getVid());
        main.initAddStartQuestionnaireVid(this.getVid());

        this.setMainQuestionNodeList(list);
    }
    public void editMainQuestion(@Valid EditMainQuestionAction action, Supplier<Long> idSupplier) {
        QuestionNode mainNode = this.findNodeByQuestionId(action.getQuestionId());
        if (mainNode == null) {
            throw new NotFoundException("Not found the main question.");
        }
        QuestionNode node = mainNode.editQuestionContent(action.getQuestion(), action.getOperator(),
                this.createdTime, idSupplier);
        this.modifyActionNodeList.add(node);
    }


    public void editAnswer(@Valid @NotEmpty List<EditAnswerAction> actionList, Supplier<Long> idSupplier) {
        actionList.forEach(action -> this.editAnswer(action, idSupplier));
    }
    public void editAnswer(@Valid @NotNull EditAnswerAction action, Supplier<Long> idSupplier) {
        QuestionNode node = this.findNodeByQuestionId(action.getQuestionId());
        if (node == null) {
            throw new NotFoundException("Not found the question.");
        }
        if (!Objects.equals(node.getQuestionVid(), action.getBasedQuestionVid())) {
            throw new IllegalRequestException("The question's answer has been modified.");
        }
        boolean success = node.editAnswer(action.getAnswer(), action.getOperator(), action.getActionTime(), idSupplier);
        if (success) {
            this.modifyActionNodeList.add(node);
        }
    }


    public void addSubQuestion(AddSubQuestionAction action, Supplier<Long> idSupplier) {
        QuestionNode mainNode = this.findNodeByQuestionId(action.getMainQuestionId());
        if (mainNode == null) {
            throw new NotFoundException("Not found the main question.");
        }
        QuestionNode sub = mainNode.addSub(action.getOperator(), action.getSubQuestion(),
                action.getBeforeQuestionId(), this.getCreatedTime(), idSupplier);
        this.addActionNodeList.add(sub);
    }

    public void deleteSubQuestion(@Valid DeleteSubQuestionAction action) {
        QuestionNode mainNode = this.findNodeByQuestionId(action.getMainQuestionId());
        if (mainNode == null) {
            throw new NotFoundException("Not found the main question.");
        }
        QuestionNode deleted = mainNode.deleteSub(action.getSubQuestionId());
        this.deleteActionNodeList.add(deleted);
    }

    public void editSubQuestion(@Valid EditSubQuestionAction action, Supplier<Long> idSupplier) {
        QuestionNode mainNode = this.findNodeByQuestionId(action.getMainQuestionId());
        if (mainNode == null) {
            throw new NotFoundException("Not found the main question.");
        }
        QuestionNode subNode = mainNode.editSub(action.getOperator(), action.getSubQuestionId(),
                action.getSubQuestion(), this.createdTime, idSupplier);
        this.modifyActionNodeList.add(subNode);
    }

    public void reorderSubQuestion(@Valid ReorderSubQuestionAction action) {
        QuestionNode mainNode = this.findNodeByQuestionId(action.getMainQuestionId());
        if (mainNode == null) {
            throw new NotFoundException("Not found the main question.");
        }
        mainNode.reorderSub(action.getOrderedSubQuestionIdList());
    }

    synchronized
    public void deleteMainQuestion(Long questionId) {
        Objects.requireNonNull(questionId);
        QuestionNode toDelete = this.findMainQuestionById(questionId);
        if (toDelete == null) {
            throw new NotFoundException("The question not existing.");
        }
        List<QuestionNode> list = new ArrayList<>(this.getMainQuestionNodeList().size());
        for (QuestionNode questionNode : this.getMainQuestionNodeList()) {
            if (questionNode != toDelete) {
                list.add(questionNode);
            }
            boolean samePrinciple = questionNode.getPrinciple() == toDelete.getPrinciple();
            if (samePrinciple) {
                if (questionNode.getSerialOfPrinciple() > toDelete.getSerialOfPrinciple()) {
                    questionNode.setSerialOfPrinciple(questionNode.getSerialOfPrinciple() - 1);
                }
            }
        }
        this.mainQuestionNodeList = list;
    }

    synchronized
    public void reorder(Principle principle, AssessmentStep step, List<Long> newOrderQuestionIdList) {
        List<QuestionNode> currentMainList = this.find(principle, step);

        List<QuestionNode> newOrderedList = new ArrayList<>(currentMainList.size());
        if (currentMainList.isEmpty()) {
            throw new NotFoundException("Not found the questions.");
        }
        int serialOfPrinciple = currentMainList.get(0).getSerialOfPrinciple();
        for (Long questionId : newOrderQuestionIdList) {
            Optional<QuestionNode> optional = currentMainList.stream()
                    .filter(q -> Objects.equals(q.getQuestionId(), questionId))
                    .findFirst();
            if (optional.isPresent()) {
                QuestionNode questionNode = optional.get();
                newOrderedList.add(questionNode);
                questionNode.configureSerialOfPrinciple(serialOfPrinciple);
                ++serialOfPrinciple;
            } else {
                log.warn("Not found the question[{}]. It may be deleted.", questionId);
            }
        }
        if (newOrderedList.size() != currentMainList.size()) {
            throw new HasBeenModifiedException("Some new questions have been added.");
        }
        this.mainQuestionNodeList = this.mainQuestionNodeList.stream().sorted().collect(Collectors.toList());
    }

    // TODO: 2023/2/13 to private
    public void deleteSubQuestions(Long mainQuestionId, List<Long> deleteList) {
        Objects.requireNonNull(mainQuestionId);
        Objects.requireNonNull(deleteList);
        if (deleteList.isEmpty()) {
            return;
        }
        QuestionNode main = this.findMainQuestionById(mainQuestionId);
        if (main == null) {
            log.warn("");
            return;
        }

        List<QuestionNode> newSubList = new ArrayList<>();
        int subSerial = 1;
        for (QuestionNode sub : main.getSubList()) {
            if (deleteList.contains(sub.getQuestionId())) {
                continue;
            }
            sub.setSubSerial(subSerial);
            ++subSerial;
            newSubList.add(sub);
        }
        main.setSubList(newSubList);
    }


    public List<QuestionNode> edite(Long mainQuestionId, Map<Long, String> editMap, Supplier<Long> idGenerator) {
        Objects.requireNonNull(mainQuestionId);
        Objects.requireNonNull(editMap);
        Objects.requireNonNull(idGenerator);
        if (editMap.isEmpty()) {
            return Collections.emptyList();
        }
        QuestionNode main = this.findMainQuestionById(mainQuestionId);
        if (main == null) {
            throw new NotFoundException("Not found the question.");
        }
        Map<Long, QuestionNode> allNode = main.toIdNodeMap();
        List<QuestionNode> editedNodeList = new ArrayList<>();

        // create version
        for (Map.Entry<Long, String> entry : editMap.entrySet()) {
            Long questionId = entry.getKey();
            String questionContent = entry.getValue();

            QuestionNode node = allNode.get(questionId);
            if (node == null) {
                throw new NotFoundException("Not found the question.");
            }

            QuestionVersion newVersion = node.getQuestionVersion().clone();
            newVersion.setVid(idGenerator.get());
            newVersion.setContent(questionContent);
            newVersion.setContentEditTime(this.getCreatedTime());
            newVersion.setContentEditUserId(this.creatorUserId);

            node.setQuestionVersion(newVersion);
            node.setQuestionVid(newVersion.getVid());

            //meta
            QuestionMeta meta = node.getMeta();
            meta.setCurrentVid(newVersion.getVid());

            editedNodeList.add(node);
        }
        return editedNodeList;
    }

}
