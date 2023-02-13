package org.veritas.assessment.biz.dto.v2.questionnaire;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@NoArgsConstructor
public class QuestionEditContentDto {
    @NotNull
    private Integer projectId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long basedQuestionVid;
    @NotNull
    private String question;

    private Boolean edited;

    List<SubQuestionEditDto> existingSubList;

    @JsonSetter(nulls = Nulls.AS_EMPTY)
    List<Long> deletedSubList;

    @Data
    public static class SubQuestionEditDto {
        // null, if the sub is added.
        private Long questionId;
        // null, if the sub is added.
        private Long basedQuestionVid;

        private String question;
    }

}
