package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubQuestionEditDto {
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    private String question;
}
