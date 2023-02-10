package org.veritas.assessment.biz.dto.v2.questionnaire;

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

    private boolean edited;

    List<SubQuestionEditDto> existingSubList;

    List<Long> deletedSubList;

    @Data
    public static class SubQuestionEditDto {
        // null, if the sub is added.
        private Long questionId;

        // null, if the sub is added.
        private Long basedQuestionVid;

        private Boolean edited;

        private Boolean add;

        private String question;


    }

}
