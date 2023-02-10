package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.Objects;

/**
 * Question Version for Question
 */
@Data
@Slf4j
@NoArgsConstructor
@TableName(autoResultMap = true)
public class QuestionVersion implements Comparable<QuestionVersion>, Cloneable {
    /**
     * Version Id
     */
    @TableId(type = IdType.INPUT)
    private Long vid;

    private Long questionId;

    private Long mainQuestionId;

    private Integer projectId;

    /** question content */
    private String content;
    private boolean contentEditable = DEFAULT_EDITABLE;
    private Integer contentEditUserId;
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date contentEditTime;

    /** Hint for user to answer the question */
    private String hint;

    private String answer;
    private Integer answerEditUserId;
    private boolean answerRequired = DEFAULT_ANSWER_REQUIRED;
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date answerEditTime;

    private static final boolean DEFAULT_EDITABLE = false;
    private static final boolean DEFAULT_ANSWER_REQUIRED = false;

    public QuestionVersion(TemplateQuestion templateQuestion) {
        this.setContent(templateQuestion.getContent());
        this.setHint(templateQuestion.getHint());
        this.setContentEditable(templateQuestion.isEditable());
        this.setAnswerRequired(templateQuestion.isAnswerRequired());
    }

    public QuestionVersion(QuestionVersion other) {
        this.setQuestionId(other.getQuestionId());
        this.setMainQuestionId(other.getMainQuestionId());
        this.setProjectId(other.getProjectId());

        this.setContent(other.getContent());
        this.setContentEditable(other.isContentEditable());
        this.setContentEditTime(other.getContentEditTime());
        this.setContentEditUserId(other.getContentEditUserId());
        this.setHint(other.getHint());

        this.setAnswer(other.getAnswer());
        this.setAnswerEditUserId(other.getAnswerEditUserId());
        this.setAnswerRequired(other.isAnswerRequired());
        this.setAnswerEditTime(other.getAnswerEditTime());
    }

    @JsonIgnore
    public boolean isMain() {
        if (this.getQuestionId() == null || this.getMainQuestionId() == null) {
            throw new IllegalStateException();
        }
        return Objects.equals(this.getQuestionId(), this.getMainQuestionId());
    }

    @JsonIgnore
    public boolean isSub() {
        return !isMain();
    }

    // compare version id for the diff version of same question
    @Override
    public int compareTo(QuestionVersion o) {
        boolean sameProjectId = Objects.equals(this.getProjectId(), o.getProjectId());
        boolean sameQid = Objects.equals(this.getQuestionId(), o.getQuestionId());
        if (!sameProjectId || !sameQid) {
            throw new IllegalArgumentException();
        }
        return this.getVid().compareTo(o.getVid());
    }

    @Override
    public QuestionVersion clone() {
        try {
            return (QuestionVersion) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException(exception);
        }
    }
}

