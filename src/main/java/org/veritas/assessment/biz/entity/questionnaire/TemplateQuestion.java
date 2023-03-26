package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.exception.IllegalRequestException;
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
public class TemplateQuestion implements Comparable<TemplateQuestion> {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer templateId;

    private Integer mainQuestionId;
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

    private String answerTemplateFilename;

    @TableField(exist = false)
    private List<TemplateQuestion> subList = Collections.emptyList();

    @TableLogic
    private boolean deleted = false;

    public List<TemplateQuestion> getSubList() {
        if (this.subList == null || this.subList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(subList);
        }
    }

    public List<TemplateQuestion> plainList() {
        List<TemplateQuestion> list = new ArrayList<>(this.getSubList().size());
        list.add(this);
        list.addAll(this.getSubList());
        return Collections.unmodifiableList(list);
    }

    public void setSerialOfPrinciple(int serialOfPrinciple) {
        this.serialOfPrinciple = serialOfPrinciple;
        if (this.isMain()) {
            this.getSubList().forEach(s -> s.setSerialOfPrinciple(serialOfPrinciple));
        }
    }

    public void setMainQuestionId(Integer mainQuestionId) {
        this.mainQuestionId = mainQuestionId;
        if (this.isMain()) {
            this.getSubList().forEach(s -> s.mainQuestionId = mainQuestionId);
        }
    }

    public void addSub(TemplateQuestion sub) {
        if (!this.contain(sub)) {
            throw new IllegalArgumentException();
        }
        if (this.getSubList().contains(sub)) {
            return;
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

    public String serial() {
        return this.principle.getShortName() + this.serialOfPrinciple;
    }

    public boolean contain(TemplateQuestion sub) {
        Objects.requireNonNull(sub);
        if (!this.isMain()) {
            return false;
        }
        if (sub.isMain()) {
            return false;
        }
        boolean sameTemplate = Objects.equals(this.getTemplateId(), sub.getTemplateId());
        boolean samePrinciple = this.getPrinciple() == sub.getPrinciple();
        boolean sameStep = this.getStep() == sub.getStep();
        boolean sameSerial = this.getSerialOfPrinciple() == sub.getSerialOfPrinciple();
        return sameTemplate && samePrinciple && sameStep && sameSerial;

    }

    public String defaultFreeMarkTemplate() {
        if (this.isMain()) {
            return String.format("%s/%s.ftl", this.principle.getShortName(), this.serial());
        } else {
            return String.format("%s/%s_S%d.ftl",
                    this.principle.getShortName(), this.serial(), this.subSerial);
        }
    }

    public void addNewSub(TemplateQuestion sub) {
        if (!this.contain(sub)) {
            throw new IllegalArgumentException();
        }
        List<TemplateQuestion> temp = new ArrayList<>(subList);
        temp.forEach(s -> {
            if (s.getSubSerial() >= sub.getSubSerial()) {
                s.setSubSerial(s.getSubSerial() + 1);
            }
        });
        temp.add(sub);
        temp = temp.stream().sorted().collect(Collectors.toList());
        int seq = 1;
        for (TemplateQuestion question : temp) {
            question.setSubSerial(seq);
            ++seq;
        }
    }

    public void reorderSub(List<Integer> newOrderList) {
        if (isSub()) {
            throw new IllegalStateException();
        }
        if (newOrderList == null || newOrderList.isEmpty()) {
            throw new IllegalArgumentException();
        }
        List<TemplateQuestion> newOrderSubList = new ArrayList<>();
        for (Integer id : newOrderList) {
            for (TemplateQuestion sub : this.getSubList()) {
                if (Objects.equals(sub.getId(), id)) {
                    newOrderSubList.add(sub);
                    break;
                }
            }
        }
        if (this.getSubList().size() != newOrderSubList.size()) {
            throw new IllegalRequestException("");
        }
        int subSeq = 1;
        for (TemplateQuestion sub : newOrderSubList) {
            sub.setSubSerial(subSeq);
            ++subSeq;
        }
        this.setSubList(Collections.unmodifiableList(newOrderSubList));
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
        List<TemplateQuestion> allSubList = new ArrayList<>();
        for (TemplateQuestion q : templateQuestionList) {
            if (q.isMain()) {
                mainList.add(q);
            } else {
                allSubList.add(q);
            }
        }
        mainList = mainList.stream().sorted().collect(Collectors.toList());
        for (TemplateQuestion sub : allSubList) {
            for (TemplateQuestion main : mainList) {
                if (sub.isSub() && main.contain(sub)) {
                    main.addSub(sub);
                }
            }
        }
        return mainList;
    }

    public TemplateQuestion create() {
        TemplateQuestion newOne = new TemplateQuestion();
        BeanUtils.copyProperties(this, newOne, "subList");
        newOne.setId(null);
        newOne.setTemplateId(null);
        if (this.isSub()) {
            newOne.subList = null;
        } else {
            List<TemplateQuestion> list = new ArrayList<>();
            this.subList.forEach(e -> list.add(e.create()));
            newOne.setSubList(list);
        }
        return newOne;
    }

    public int nextSubSerial() {
        if (!isMain()) {
            throw new IllegalStateException();
        }
        List <TemplateQuestion> list = this.getSubList();
        if (list == null || list.isEmpty()) {
            return 1;
        } else {
            return list.get(list.size() - 1).getSubSerial() + 1;
        }
    }
}