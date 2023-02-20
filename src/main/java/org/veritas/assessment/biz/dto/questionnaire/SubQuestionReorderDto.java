package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class SubQuestionReorderDto {
    @NotNull
    private Long basedQuestionnaireVid;
    @NotNull
    private Long mainQuestionId;
    @NotNull
    private List<Long> orderedSubQuestionIdList;
}
