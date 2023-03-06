package org.veritas.assessment.biz.dto.questionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateQuestionDto {
    private Integer id;
    private Integer templateId;
    private AssessmentStep step;
    private String serial;
    private int subSerial;
    private String question;
    private String hint;
    private boolean editable;
    private int editorUserId;
    private Date editTime;
    private List<TemplateQuestionDto> subQuestionList;

    public TemplateQuestionDto(TemplateQuestion question) {
        BeanUtils.copyProperties(question, this);
        this.question = question.getContent();
        this.serial = question.serial();
        this.editable = question.isEditable();
        if (question.getSubList() != null && !question.getSubList().isEmpty()) {
            this.subQuestionList = question.getSubList().stream().map(TemplateQuestionDto::new).collect(Collectors.toList());
        }
    }
}
