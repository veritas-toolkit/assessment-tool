package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;

import java.util.List;

/**
 * Table of content for questionnaire.
 *
 * Structure: principle -> step -> main question
 */
@Data
public class QuestionnaireTocDto {

    private int projectId;

    private int questionnaireVid;

    private List<PrinciplePart> principlePartList;

    @Data
    public static class PrinciplePart {
        // Generic
        private String principle;

        // G, F, EA, T
        private String principleShortName;

        private List<Step> stepList;
    }

    @Data
    public static class Step {
        // 2
        private int serialNo;
        // Prepare Input Data
        private String name;

        List<MainQuestion> mainQuestionList;
    }

    @Data
    public static class MainQuestion {
        private int id;
        private int vid;
        // F5
        private String serial;
        // Have you documented how are these impacts being mitigated?
        private String content;

    }

    public QuestionnaireTocDto() {
    }

}