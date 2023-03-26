package org.veritas.assessment.biz.action;

import lombok.Data;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ReorderSubQuestionAction {
    @NotNull
    private User operator;
    @NotNull
    private Integer projectId;
    @NotNull
    private Long basedQuestionnaireVid;
    @NotNull
    private Long mainQuestionId;
    @NotNull
    private List<Long> orderedSubQuestionIdList;
}
