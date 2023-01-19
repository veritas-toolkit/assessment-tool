package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.Objects;

@Data
public class TemplateQuestion implements Comparable<TemplateQuestion>{
    @TableId(type = IdType.AUTO)
    private Integer id;
    private int templateId;
//    private Integer mainQuestionId;
    private Principle principle;

    private AssessmentStep step;
    private int serialOfPrinciple;
    private int subSerial;

    private String content;
    private String hint;
    private boolean editable;
    private int editorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date editTime;
    private boolean answerRequired;

    @TableLogic
    private boolean deleted = false;

    public boolean isMain() {
        return this.getSubSerial() == 0;
    }

    @Override
    public int compareTo(TemplateQuestion o) {
        boolean sameTemplate = Objects.equals(this.getTemplateId(), o.getTemplateId());
        boolean samePrinciple = this.getPrinciple() == o.getPrinciple();
        if (!sameTemplate || !samePrinciple) {
            throw new IllegalArgumentException();
        }
        boolean sameSerialOfPrinciple = this.getSerialOfPrinciple() == o.getSerialOfPrinciple();
        if (!sameSerialOfPrinciple) {
            return this.getSerialOfPrinciple() - o.getSerialOfPrinciple();
        } else {
            return this.getSubSerial() - o.getSubSerial();
        }
    }
}