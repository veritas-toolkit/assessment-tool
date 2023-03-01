package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class TemplateQuestionnaireEditDto {
    @NotNull
    private Integer templateId;
    @NotEmpty
    private String name;
    @NotEmpty
    private String description;
}
