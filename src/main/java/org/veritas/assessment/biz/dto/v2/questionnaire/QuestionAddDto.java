package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.List;

@Data
public class QuestionAddDto {

    @NotNull(message = "The project id can't be null.")
    private Integer projectId;
    @NotNull(message = "The principle can't be null.")
    private Principle principle;

    @NotNull(message = "The step can't be null.")
    private AssessmentStep step;

//    private Integer stepSerial = -1;

    @NotNull(message = "The question content can't be null or empty.")
    @NotEmpty(message = "The question content can't be null or empty.")
    private String question;

    private List<String> subQuestionList;

    public AddMainQuestionAction toAction() {
        AddMainQuestionAction action = new AddMainQuestionAction();
        action.setProjectId(this.projectId);
        action.setPrinciple(this.principle);
        action.setStep(this.step);
        action.setQuestion(this.getQuestion());
        action.setSubQuestionList(Collections.unmodifiableList(this.subQuestionList));
        return action;
    }
}
