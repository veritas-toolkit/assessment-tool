package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * DTO for reorder the question
 */
@Data
public class QuestionReorderDto {
    @NotNull(message = "The project id cannot be null.")
    private Integer projectId;

    @NotNull(message = "The principle cannot be null.")
    private Principle principle;

    @NotNull(message = "The assessment step cannot be null.")
    private AssessmentStep step;

    private List<Long> questionIdList;
}
