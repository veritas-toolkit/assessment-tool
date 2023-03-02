package org.veritas.assessment.biz.dto.questionnaire;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TemplateQuestionDto {
    private Integer id;
    private Integer templateId;
    private Principle principle;
    private AssessmentStep step;
    private int serialOfPrinciple;
    private int subSerial;
    private String content;
    private String hint;
    private boolean editable;
    private int editorUserId;
    private Date editTime;
    private List<TemplateQuestionDto> subList;

    public TemplateQuestionDto(TemplateQuestion question) {
        BeanUtils.copyProperties(question, this);
        if (question.getSubList() != null && !question.getSubList().isEmpty()) {
            this.subList = question.getSubList().stream().map(TemplateQuestionDto::new).collect(Collectors.toList());
        }
    }
}
