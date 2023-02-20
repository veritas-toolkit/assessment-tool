package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Table of content for questionnaire.
 *
 * Structure: principle -> step -> main question
 */
@Data
public class TemplateQuestionnaireTocDto {

    private int templateId;

    private String name;

    private String description;

    private Map<String, String> principles;

    private List<PrinciplePart> principlePartList;

    @Setter(value = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PUBLIC)
    public static class PrinciplePart {
        // Generic
        private String principle;

        // G, F, EA, T
        private String principleShortName;

        private List<Step> stepList = new ArrayList<>();

        void addStep(Step step) {
            Objects.requireNonNull(step);
            stepList.add(step);
        }
    }

    @Setter(value = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PUBLIC)
    public static class Step {
        // 2
        private int serial;
        // Prepare Input Data
        private String step;

        List<MainQuestion> mainQuestionList = new ArrayList<>();
        private Step() {
            // yes, do nothing.
        }

        void addQuestion(MainQuestion question) {
            Objects.requireNonNull(question);
            this.mainQuestionList.add(question);
        }
    }

    @Setter(value = AccessLevel.PRIVATE)
    @Getter(value = AccessLevel.PUBLIC)
    public static class MainQuestion {
        private int id;
        // F5
        private String serialTitle;
        private String content;
        private boolean editable;

        private MainQuestion(TemplateQuestion q) {
            this.id = q.getId();
            this.serialTitle = q.getPrinciple().getShortName() + q.getSerialOfPrinciple();
            this.content = q.getContent();
            this.editable = q.isEditable();
        }
    }

    public TemplateQuestionnaireTocDto(TemplateQuestionnaire questionnaire) {
        this.templateId = questionnaire.getId();
        this.name = questionnaire.getName();
        this.description = questionnaire.getDescription();
        this.principles = new LinkedHashMap<>();
        Arrays.stream(Principle.values()).forEach(e -> this.principles.put(e.getShortName(), e.getFullname()));

        List<PrinciplePart> partList = new ArrayList<>();
        for (Principle principle : Principle.values()) {
            PrinciplePart part = new PrinciplePart();
            part.setPrinciple(principle.getFullname());
            part.setPrincipleShortName(principle.getShortName());
            List<TemplateQuestion> questionList = questionnaire.getMainQuestionList().stream()
                    .filter(q -> q.getPrinciple() == principle)
                    .collect(Collectors.toList());
            for (AssessmentStep s : AssessmentStep.values()) {
                Step step = new Step();
                step.setStep(s.getDescription());
                step.setSerial(s.getStepSerial());
                questionList.stream()
                        .filter(q -> q.getStep() == s && q.isMain())
                        .sorted()
                        .forEach(q -> step.addQuestion(new MainQuestion(q)));
                part.addStep(step);
            }
            partList.add(part);
        }
        this.principlePartList = Collections.unmodifiableList(partList);

    }

}