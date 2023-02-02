package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class TemplateQuestionnaire {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private int businessScenario;

    private QuestionnaireTemplateType type;

    private String description;


    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    @TableField(exist = false)
    @Setter(AccessLevel.NONE)
    private List<TemplateQuestion> mainQuestionList;

    public void addAllQuestionList(List<TemplateQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            this.mainQuestionList = Collections.emptyList();
        }
        this.mainQuestionList = TemplateQuestion.toStructure(questionList);
    }

}