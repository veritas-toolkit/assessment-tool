package org.veritas.assessment.biz.dto.v2.questionnaire;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
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
public class QuestionnaireTocDto {

    private Integer projectId;

    private Long questionnaireVid;

    private UserSimpleDto creator;

    private Date createdTime;

    // entry example: G -> Generic
    private Map<String, String> principles;

    private Map<String, PrincipleAssessment> principleAssessments;

    @JsonIgnore
    List<Principle> supportPrincipleList;

    public QuestionnaireTocDto(QuestionnaireVersion q, Project project, UserSimpleDto userSimpleDto) {
        this.projectId = q.getProjectId();
        this.questionnaireVid = q.getVid();
        this.creator = userSimpleDto;
        this.createdTime = q.getCreatedTime();

        supportPrincipleList = project.principles();
        principles = supportPrincipleList.stream().collect(Collectors.toMap(
                Principle::getShortName,
                Principle::getDescription,
                (value1, value2) -> value1,
                LinkedHashMap::new));
        this.fillQuestions(q.getMainQuestionNodeList());
    }

    public void fillQuestions(List<QuestionNode> questionNodeList) {
        Objects.requireNonNull(questionNodeList);
        List<QuestionNode> list = questionNodeList.stream()
                .filter(node -> {
                    boolean sameProject = Objects.equals(this.getProjectId(), node.getProjectId());
                    boolean sameQ = Objects.equals(this.getQuestionnaireVid(), node.getQuestionnaireVid());
                    return sameProject && sameQ;
                })
                .collect(Collectors.toList());
        this.principleAssessments = supportPrincipleList.stream()
                .collect(Collectors.toMap(
                        Principle::getShortName,
                        p -> new PrincipleAssessment(p, questionNodeList.stream()
                                .filter(q -> p == q.getPrinciple())
                                .collect(Collectors.toList())),
                        (value1, value2) -> value1,
                        LinkedHashMap::new));
    }

    @Data
    public static class PrincipleAssessment {
        // Generic
        private String principle;

        // G, F, EA, T
        private String principleShortName;

        private List<Step> stepList;

        public PrincipleAssessment(Principle p, List<QuestionNode> questionNodeList) {
            Objects.requireNonNull(p);
            Objects.requireNonNull(questionNodeList);
            this.principle = p.getDescription();
            this.principleShortName = p.getShortName();

            List<QuestionNode> list = questionNodeList.stream()
                    .filter(node -> p == node.getPrinciple())
                    .collect(Collectors.toList());
            this.stepList = Arrays.stream(AssessmentStep.values())
                    .map(s -> new Step(s, list)).collect(Collectors.toList());
        }
    }

    @Data
    public static class Step {
        // 2
        private int serialNo;
        // Prepare Input Data
        private String step;

        List<MainQuestion> mainQuestionList;

        public Step(AssessmentStep assessmentStep, List<QuestionNode> questionNodeList) {
            Objects.requireNonNull(assessmentStep);
            Objects.requireNonNull(questionNodeList);
            this.serialNo = assessmentStep.getStepSerial();
            this.step = assessmentStep.getDescription();
            this.mainQuestionList = questionNodeList.stream()
                    .filter(node -> assessmentStep == node.getStep())
                    .map(MainQuestion::new)
                    .collect(Collectors.toList());
        }
    }

    @Data
    public static class MainQuestion {
        private Long id;
        private Long vid;
        // F5
        private String serial;
        // Have you documented how are these impacts being mitigated?
        private String question;

        public MainQuestion(QuestionNode questionNode) {
            this.id = questionNode.getQuestionId();
            this.vid = questionNode.getQuestionVid();
            this.serial = questionNode.serial();
            if (questionNode.getQuestionVersion() != null) {
                this.question = questionNode.getQuestionVersion().getContent();
            }
        }

        static List<MainQuestion> convert(List<QuestionNode> questionNodeList) {
            if (questionNodeList == null || questionNodeList.isEmpty()) {
                return Collections.emptyList();
            }
            return questionNodeList.stream()
                    .map(MainQuestion::new)
                    .collect(Collectors.toList());
        }
    }
}