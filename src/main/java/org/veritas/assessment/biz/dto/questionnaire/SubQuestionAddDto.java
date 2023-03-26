package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubQuestionAddDto {
    @NotNull
    private String question;
    // if null, then append as last sub.
    private Long beforeQuestionId;
}
