package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;

import java.util.ArrayList;
import java.util.Arrays;
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
public class TemplateQuestionnaireTocDto {

    private int templateId;

    private String name;

    private String description;

    private QuestionnaireTemplateType type;

    private Map<String, String> principles;

    private Map<String, PrinciplePart> principleAssessments;

    @Data
    @NoArgsConstructor
    public static class PrinciplePart {
        // Generic
        private String principle;

        // G, F, EA, T
        private String principleShortName;

        private List<Step> stepList = new ArrayList<>();

        public PrinciplePart(Principle principle, List<TemplateQuestion> questionList) {
            Objects.requireNonNull(principle);
            this.principle = principle.getFullname();
            this.principleShortName = principle.getShortName();
            this.stepList = Arrays.stream(AssessmentStep.values())
                    .map(step -> new Step(principle, step, questionList)).collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    public static class Step {
        // 2
        private int serialNo;
        // Prepare Input Data
        private String step;

        List<MainQuestion> mainQuestionList = new ArrayList<>();

        public Step(Principle principle, AssessmentStep assessmentStep, List<TemplateQuestion> questionList) {
            Objects.requireNonNull(principle);
            Objects.requireNonNull(assessmentStep);
            Objects.requireNonNull(questionList);
            this.serialNo = assessmentStep.getStepSerial();
            this.step = assessmentStep.getDescription();
            this.mainQuestionList = questionList.stream()
                    .filter(q -> q.getPrinciple() == principle)
                    .filter(q -> q.getStep() == assessmentStep)
                    .map(MainQuestion::new).collect(Collectors.toList());
        }
    }

    @Data
    @NoArgsConstructor
    public static class MainQuestion {
        private int id;
        // F5
        private String serial;
        private String question;
        private boolean editable;

        private MainQuestion(TemplateQuestion q) {
            this.id = q.getId();
            this.serial = q.getPrinciple().getShortName() + q.getSerialOfPrinciple();
            this.question = q.getContent();
            this.editable = q.isEditable();
        }
    }

    public TemplateQuestionnaireTocDto(TemplateQuestionnaire questionnaire) {
        this.templateId = questionnaire.getId();
        this.name = questionnaire.getName();
        this.description = questionnaire.getDescription();
        this.type = questionnaire.getType();
        this.principles = new LinkedHashMap<>();
        Arrays.stream(Principle.values()).forEach(e -> this.principles.put(e.getShortName(), e.getFullname()));

        this.principleAssessments = Arrays.stream(Principle.values())
                .collect(Collectors.toMap(
                        Principle::getShortName,
                        p -> new PrinciplePart(p, questionnaire.getMainQuestionList().stream()
                                .filter(q -> q.getPrinciple() == p)
                                .collect(Collectors.toList()))
                ));
    }

}