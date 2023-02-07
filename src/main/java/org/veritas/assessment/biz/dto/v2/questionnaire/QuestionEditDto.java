package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class QuestionEditDto {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    private String answer;
}
