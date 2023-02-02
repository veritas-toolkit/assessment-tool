package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor
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



    @TableField(exist = false)
    private List<TemplateQuestion> subList = Collections.emptyList();

    @TableLogic
    private boolean deleted = false;

    public void addSub(TemplateQuestion sub) {
        if (!this.contain(sub)) {
            throw new IllegalArgumentException();
        }
        List<TemplateQuestion> temp = new ArrayList<>(subList);
        temp.add(sub);
        this.subList = temp.stream().sorted().collect(Collectors.toList());
    }


    public boolean isMain() {
        return this.getSubSerial() == 0;
    }

    public boolean isSub() {
        return !this.isMain();
    }

    public boolean contain(TemplateQuestion sub) {
        Objects.requireNonNull(sub);
        if (!this.isMain()) {
            return false;
        }
        if (sub.isMain()) {
            return false;
        }
        boolean sameTemplate = this.getTemplateId() == sub.getTemplateId();
        boolean samePrinciple = this.getPrinciple() == sub.getPrinciple();
        boolean sameStep = this.getStep() == sub.getStep();
        boolean sameSerial = this.getSerialOfPrinciple() == sub.getSerialOfPrinciple();
        return sameTemplate && samePrinciple && sameStep && sameSerial;

    }


    @Override
    public int compareTo(TemplateQuestion o) {
        boolean sameTemplate = Objects.equals(this.getTemplateId(), o.getTemplateId());
        if (!sameTemplate) {
            throw new IllegalArgumentException();
        }
        boolean samePrinciple = this.getPrinciple() == o.getPrinciple();
        if (!samePrinciple) {
            return this.getPrinciple().compareTo(o.getPrinciple());
        }
        boolean sameSerialOfPrinciple = this.getSerialOfPrinciple() == o.getSerialOfPrinciple();
        if (!sameSerialOfPrinciple) {
            return this.getSerialOfPrinciple() - o.getSerialOfPrinciple();
        } else {
            return this.getSubSerial() - o.getSubSerial();
        }
    }

    public static List<TemplateQuestion> toStructure(List<TemplateQuestion> templateQuestionList) {
        if (templateQuestionList == null || templateQuestionList.isEmpty()) {
            return Collections.emptyList();
        }
        List<TemplateQuestion> mainList = new ArrayList<>();
        for (TemplateQuestion q : templateQuestionList) {
            if (q.isMain()) {
                mainList.add(q);
            }
        }
        mainList = mainList.stream().sorted().collect(Collectors.toList());;
        for (TemplateQuestion main : mainList) {
            for (TemplateQuestion sub : templateQuestionList) {
                if (sub.isSub() && main.contain(sub)) {
                    main.addSub(sub);
                }
            }
        }
        return mainList;
    }
}