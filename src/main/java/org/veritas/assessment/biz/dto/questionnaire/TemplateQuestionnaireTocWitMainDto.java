package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
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
public class TemplateQuestionnaireTocWitMainDto {
    private TemplateQuestionnaireTocDto toc;
    private TemplateQuestionDto question;

    public TemplateQuestionnaireTocWitMainDto(TemplateQuestionnaire questionnaire, TemplateQuestion question) {
        Objects.requireNonNull(questionnaire);
        this.toc = new TemplateQuestionnaireTocDto(questionnaire);
        if (question != null) {
            this.question = new TemplateQuestionDto(question);
        }
    }
}