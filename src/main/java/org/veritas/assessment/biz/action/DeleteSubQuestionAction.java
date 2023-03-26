package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;

@Data
public class DeleteSubQuestionAction {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long mainQuestionId;
    @NotNull
    private Long subQuestionId;
    @NotNull
    private User operator;
}
