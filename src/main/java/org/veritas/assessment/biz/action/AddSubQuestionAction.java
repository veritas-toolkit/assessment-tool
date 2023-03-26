package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;

@Data
public class AddSubQuestionAction {
    @NotNull
    private Integer projectId;

    @NotNull
    private Long mainQuestionId;

    @NotNull
    private String subQuestion;

    // if null, then append as last sub.
    private Long beforeQuestionId;

    @NotNull
    private User operator;

}
