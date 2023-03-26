package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;

@Data
public class EditSubQuestionAction {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long basedQuestionnaireVid;
    @NotNull
    private Long mainQuestionId;
    @NotNull
    private Long subQuestionId;
    @NotNull
    private Long basedSubQuestionVid;

    private String subQuestion;
    @NotNull
    private User operator;
}
