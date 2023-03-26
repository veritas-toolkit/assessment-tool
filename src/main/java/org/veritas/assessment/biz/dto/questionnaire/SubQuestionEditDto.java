package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SubQuestionEditDto {
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    @NotEmpty
    private String question;
}
