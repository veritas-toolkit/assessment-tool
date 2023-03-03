package org.veritas.assessment.biz.dto.questionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateSubQuestionAddDto {
    @NotNull(message = "The template id can't be null.")
    private Integer templateId;
    @NotNull(message = "The main question id can't be null.")
    private Integer mainQuestionId;
    // can be null, if null then append to the end.
    private Integer subSerial;
    @NotEmpty(message = "The question content can't be null or empty.")
    private String question;
}
