package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Data
public class QuestionEditDto {
    @NotNull(message = "Bad request.")
    private Long questionId;

    @NotNull(message = "Bad request.")
    private Long basedQuestionVid;

    @NotNull(message = "The question content should not be empty.")
    @NotEmpty(message = "The question content should not be empty.")
    private String question;
}
