package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TemplateSubQuestionReorderDto {
    @NotNull(message = "The templateId cannot be null.")
    private Integer templateId;
    @NotNull(message = "The main question id cannot be null.")
    private Integer mainQuestionId;
    @NotNull(message = "The new order list cannot be null.")
    private List<Integer> newOrderList;
}
