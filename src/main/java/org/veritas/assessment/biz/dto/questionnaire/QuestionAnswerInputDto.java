package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class QuestionAnswerInputDto {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    private String answer;
}
