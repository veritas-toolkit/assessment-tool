package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;

import java.util.List;

@Data
public class QuestionAddDto {
    private Long basedQuestionnaireVid;

    // example: G, F, EA, T
    private String principle;

    private String question;

    // for main
    // 新增主问题，新增的问题再beforeMainQuestionId问题之前，如果null，则该问题在此原则最后一个
    private Long beforeMainQuestionId;

    private List<String> subQuestionList;
}
