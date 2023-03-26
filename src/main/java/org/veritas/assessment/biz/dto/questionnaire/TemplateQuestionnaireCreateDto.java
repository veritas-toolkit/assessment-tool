package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TemplateQuestionnaireCreateDto {
    private Integer basicTemplateId;
    private String name;
    private String description;
}
