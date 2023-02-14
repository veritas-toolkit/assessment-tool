package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SubQuestionAddDto {
    @NotNull
    private String question;

//    private Long beforeQuestionId;
}
