package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import java.util.Date;

@Data
@NoArgsConstructor
public class QuestionnaireRecordDto {
    private Integer projectId;

    private Long questionnaireVid;

    private Boolean exported;
    private String message;

    private UserSimpleDto creator;

    private Date createdTime;

    public QuestionnaireRecordDto(QuestionnaireVersion questionnaire) {
        this.projectId = questionnaire.getProjectId();
        this.questionnaireVid = questionnaire.getVid();
        this.message = questionnaire.getMessage();
        this.createdTime = questionnaire.getCreatedTime();
    }
}
