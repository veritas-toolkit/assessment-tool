package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAddSubDto {
    private Long basedQuestionnaireVid;
    private Long mainQuestionId;

    private String question;
    // 当前问题在此问题之前，如果为null，新增的子问题在最后
    private Long beforeQuestionId;
}
