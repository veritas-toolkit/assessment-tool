package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotNull;
import java.util.List;

// 1. 新增主问题（含子问题）
// 2. 删除主问题
// 3. 主问题顺序调整: 仅能调整同一个原则、同一个步骤中的问题顺序
// 4. 修改现存主问题(含子问题) - 以下修改内容必须上传整个主问题
//      a. 修改主问题内容
//      b. 修改子问题内容
//      c. 新增子问题
//      d. 删除子问题
//      e. 子问题顺序调整

//
@Data
@NoArgsConstructor
public class QuestionEditContentDto {
    @NotNull
    private Integer projectId;
    // 如果是null，则说明是新增的
    private Long questionId;
    // 如果是null，则说明是新增的
    private Long basedQuestionVid;

    private String question;

    private boolean edited;

    private MainQuestionSite mainQuestionSite;

    List<SubQuestionEditDto> subList;

    @Data
    public static class SubQuestionEditDto {
        // null, if the sub is added.
        private Long questionId;

        // null, if the sub is added.
        private Long basedQuestionVid;

        private String question;

        private boolean edited;
    }

    @Data
    public static class MainQuestionSite {
        @NotNull
        private Principle principle;

        @NotNull
        private AssessmentStep step;

        // if null, then append the step
        private Long beforeQuestionId;
    }
}
