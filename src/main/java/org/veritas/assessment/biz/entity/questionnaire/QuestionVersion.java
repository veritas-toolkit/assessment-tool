package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;

/**
 * Question Version for Question
 */
@Data
@Slf4j
public class QuestionVersion implements Comparable<QuestionVersion> {
    /**
     * Version Id
     */
    @TableId(type = IdType.AUTO)
    private int vid;

    private int questionId;

    private int mainQuestionId;

    private int projectId;

    /** question content */
    private String content;

    /** Hint for user to answer the question */
    private String hint;

    private int contentEditUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date contentEditTime;

    private String answer;

    private int answerEditUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date answerEditTime;

    @TableField(exist = false)
    private QuestionMeta questionMeta;



    public boolean isMain() {
        return this.getQuestionId() == this.getMainQuestionId();
    }

    public void setQuestionMeta(QuestionMeta questionMeta) {
        boolean sameQuestion = this.getQuestionId() == questionMeta.getId();
        if (!sameQuestion) {
            if (log.isDebugEnabled()) {
                log.debug("Current question id is [{}], the arg question id is [{}]",
                        this.getQuestionId(), questionMeta.getId());
            }
            throw new IllegalArgumentException("The question IDs are different.");
        }
        this.questionMeta = questionMeta;
    }

    // compare version id for the diff version of same question
    @Override
    public int compareTo(QuestionVersion o) {
        boolean sameProjectId = this.getProjectId() == o.getProjectId();
        boolean sameQid = this.getQuestionId() == o.getQuestionId();
        if (!sameProjectId || !sameQid) {
            throw new IllegalArgumentException();
        }
        return this.getVid() - o.getVid();
    }
}
