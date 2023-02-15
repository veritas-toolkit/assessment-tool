package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;

@Data
public class EditMainQuestionAction {
    @NotNull
    private Integer projectId;

    @NotNull
    private Long questionId;

    @NotNull
    private Long basedQuestionVid;

    @NotNull
    private String question;

    @NotNull
    private User operator;
}
