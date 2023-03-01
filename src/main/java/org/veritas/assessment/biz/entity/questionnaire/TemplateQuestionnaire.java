package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@TableName(autoResultMap = true)
public class TemplateQuestionnaire {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private BusinessScenarioEnum businessScenario;

    private QuestionnaireTemplateType type;

    private String description;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    private Integer creatorUserId;

    @TableLogic
    private boolean deleted = false;

    @TableField(exist = false)
    private List<TemplateQuestion> mainQuestionList;

    public List<TemplateQuestion> getMainQuestionList() {
        if (mainQuestionList == null || mainQuestionList.isEmpty()) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(mainQuestionList);
        }
    }

    public void addAllQuestionList(List<TemplateQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            this.mainQuestionList = Collections.emptyList();
        }
        this.mainQuestionList = TemplateQuestion.toStructure(questionList);
    }

    public List<TemplateQuestion> allQuestionList() {
        List<TemplateQuestion> list = new ArrayList<>();
        for (TemplateQuestion templateQuestion : this.mainQuestionList) {
            list.add(templateQuestion);
            list.addAll(templateQuestion.getSubList());
        }
        return Collections.unmodifiableList(list);
    }

    public void setId(Integer id) {
        Objects.requireNonNull(id);
        this.id = id;
        this.getMainQuestionList().forEach(q -> {
            q.setTemplateId(id);
            q.getSubList().forEach(sub -> sub.setTemplateId(id));
        });
    }

    public boolean canBeEditOrDeleted() {
        return this.type != QuestionnaireTemplateType.SYSTEM;
    }

}