package org.veritas.assessment.biz.dto.questionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateQuestionEditDto {
    /**
     * question id, including main-question and sub-question.
     */
    @NotNull
    private Integer id;
    @NotNull
    private Integer mainQuestionId;
    @NotEmpty
    private String content;
}
