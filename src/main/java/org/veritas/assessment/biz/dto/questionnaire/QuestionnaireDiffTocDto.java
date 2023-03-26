package org.veritas.assessment.biz.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Table of content for questionnaire.
 * <p>
 * Structure: principle -> step -> main question
 */
@Data
@NoArgsConstructor
public class QuestionnaireDiffTocDto {

    private Integer projectId;

    private Long basedQuestionnaireVid;

    private Long newQuestionnaireVid;

    // entry example: G -> Generic
    private Map<String, String> principles;
    private Map<String, PrincipleAssessment> principleAssessments;

    public QuestionnaireDiffTocDto(Project project,
                                   QuestionnaireVersion basedQuestionnaire,
                                   QuestionnaireVersion newQuestionnaire) {
        this.projectId = newQuestionnaire.getProjectId();
        this.basedQuestionnaireVid = basedQuestionnaire.getVid();
        this.newQuestionnaireVid = newQuestionnaire.getVid();

        List<Principle> supportPrincipleList = project.principles();

        this.principles = supportPrincipleList.stream()
                .collect(Collectors.toMap(
                        Principle::getShortName,
                        Principle::getFullname,
                        (a, b) -> a,
                        LinkedHashMap::new));

        this.principleAssessments = supportPrincipleList.stream()
                .collect(Collectors.toMap(
                        Principle::getShortName,
                        p -> new PrincipleAssessment(p, basedQuestionnaire.getMainQuestionNodeList(),
                                newQuestionnaire.getMainQuestionNodeList()),
                        (e1, e2) -> e1,
                        LinkedHashMap::new));
    }

    @Data
    public static class PrincipleAssessment {
        // Generic
        private String principle;

        // G, F, EA, T
        private String principleShortName;

        private List<Step> stepList;

        @JsonProperty
        public int diffCount() {
            if (stepList == null || stepList.isEmpty()) {
                return 0;
            }
            return stepList.stream()
                    .mapToInt(Step::diffCount)
                    .sum();
        }

        public PrincipleAssessment(Principle p, List<QuestionNode> basedList, List<QuestionNode> newList) {
            Objects.requireNonNull(p);
            this.principle = p.getFullname();
            this.principleShortName = p.getShortName();

            basedList = basedList == null ? Collections.emptyList() : basedList;
            newList = newList == null ? Collections.emptyList() : newList;

            List<QuestionNode> finalOldList = basedList;
            List<QuestionNode> finalNewList = newList;
            this.stepList = Arrays.stream(AssessmentStep.values())
                    .map(s -> {
                        List<QuestionNode> basedNodes = finalOldList.stream()
                                .filter(n -> n.getPrinciple() == p && n.getStep() == s)
                                .collect(Collectors.toList());
                        List<QuestionNode> newNodes = finalNewList.stream()
                                .filter(n -> n.getPrinciple() == p && n.getStep() == s)
                                .collect(Collectors.toList());
                        return new Step(s, basedNodes, newNodes);
                    }).collect(Collectors.toList());
        }


    }

    @Data
    public static class Step {
        // 2
        private int serialNo;
        // Prepare Input Data
        private String step;

        List<MainQuestion> mainQuestionList;

        public Step(AssessmentStep assessmentStep,
                    List<QuestionNode> basedQuestionList,
                    List<QuestionNode> newQuestionList) {
            Objects.requireNonNull(assessmentStep);
            this.serialNo = assessmentStep.getStepSerial();
            this.step = assessmentStep.getDescription();
            boolean hasBasedNode = basedQuestionList != null && !basedQuestionList.isEmpty();
            boolean hasNewNode = newQuestionList != null && !newQuestionList.isEmpty();
            if (!hasBasedNode && !hasNewNode) {
                this.mainQuestionList = Collections.emptyList();
            }
            List<MainQuestion> list = new ArrayList<>();
            if (hasNewNode) {
                newQuestionList.forEach(newQ -> {
                    QuestionNode basedNode = QuestionNode.findByQuestionId(basedQuestionList, newQ.getQuestionId());
                    MainQuestion mainQuestion = new MainQuestion(basedNode, newQ);
                    list.add(mainQuestion);
                });
            }
            // find all deleted questions.
            if (hasBasedNode) {
                basedQuestionList.forEach(old -> {
                    QuestionNode newNode = QuestionNode.findByQuestionId(newQuestionList, old.getQuestionId());
                    if (newNode == null) {
                        list.add(new MainQuestion(old, null));
                    }
                });
            }
            List<MainQuestion> sortedList = list.stream()
                    .sorted(Comparator.comparingInt(left -> left.getSerialOfPrinciple))
                    .collect(Collectors.toList());;
            this.mainQuestionList = Collections.unmodifiableList(sortedList);
        }

        public int diffCount() {
            if (this.mainQuestionList == null || this.mainQuestionList.isEmpty()) {
                return 0;
            }
            return mainQuestionList.stream()
                    .mapToInt(q -> {
                        if (q.getEditType() != null && q.getEditType().isChanged()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    })
                    .sum();
        }
    }

    @Data
    public static class MainQuestion {
        private Long id;
        private Long basedVid;
        private Long newVid;
        // F5
        private String serial;

        @JsonIgnore
        private int getSerialOfPrinciple;
        private String question;

        private EditType editType;

        public MainQuestion(QuestionNode basedQuestionNode, QuestionNode newQuestionNode) {
            if (basedQuestionNode == null && newQuestionNode == null) {
                // arg exception.
                throw new IllegalArgumentException();
            }
            if (basedQuestionNode == null) {
                // add
                this.id = newQuestionNode.getQuestionId();
                this.newVid = newQuestionNode.getQuestionVid();
                this.serial = newQuestionNode.serial();
                this.getSerialOfPrinciple= newQuestionNode.getSerialOfPrinciple();
                this.question = newQuestionNode.questionContent();
                this.editType = EditType.NEW;
            } else if (newQuestionNode == null) {
                // delete
                this.id = basedQuestionNode.getQuestionId();
                this.basedVid = basedQuestionNode.getQuestionVid();
                this.serial = basedQuestionNode.serial();
                this.getSerialOfPrinciple= basedQuestionNode.getSerialOfPrinciple();
                this.question = basedQuestionNode.questionContent();
                this.editType = EditType.DELETE;
            } else {
                // unmodified or edited
                this.id = newQuestionNode.getQuestionId();
                this.basedVid = basedQuestionNode.getQuestionVid();
                this.newVid = newQuestionNode.getQuestionVid();
                this.serial = newQuestionNode.serial();
                this.getSerialOfPrinciple= newQuestionNode.getSerialOfPrinciple();
                this.question = newQuestionNode.questionContent();
                if (basedQuestionNode.hasBeenEdited(newQuestionNode)) {
                    this.editType = EditType.EDIT;
                } else {
                    this.editType = EditType.UNMODIFIED;
                }

            }
        }


    }

    public enum EditType {
        UNMODIFIED("Unmodified", false),
        NEW("New"),
        DELETE("Deleted"),
        EDIT("Edited"),
        ;

        @Getter
        @JsonValue
        private final String type;

        @Getter
        private boolean changed = true;

        EditType(String type) {
            this.type = type;
        }

        EditType(String type, boolean changed) {
            this.type = type;
            this.changed = changed;
        }

    }
}