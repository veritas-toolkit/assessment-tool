package org.veritas.assessment.biz.action;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditAnswerAction {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    private String answer;
    @NotNull
    private User operator;
    @NotNull
    private Date actionTime;
}
