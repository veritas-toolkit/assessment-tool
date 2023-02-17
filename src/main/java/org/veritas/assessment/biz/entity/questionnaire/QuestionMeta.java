package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * Save Question metadata.
 * Every question, including main-question and sub-question, only has one record.
 */
@Data
@NoArgsConstructor
public class QuestionMeta implements Cloneable {
    @TableId(type = IdType.INPUT)
    private Long id;

    /**
     * Point to the current version id of the question.
     * <br/>
     * This property can be used as the LOCK to add a new {@link QuestionVersion} record.
     */
    private Long currentVid;

    private Integer projectId;
    private Long mainQuestionId;

    private boolean contentEditable = DEFAULT_EDITABLE;

    private boolean answerRequired = DEFAULT_ANSWER_REQUIRED;

    @TableField(exist = false)
    private String answerTemplateFilename;

    /**
     * Add this question the questionnaire from this questionnaire version.
     */
    private Long addStartQuestionnaireVid;

    /**
     * If the question be deleted
     */
    private Long deleteStartQuestionnaireVid;

    private static final boolean DEFAULT_EDITABLE = false;
    private static final boolean DEFAULT_ANSWER_REQUIRED = false;

    /**
     * Check if current question the main question.
     * @return main question return <b>true</b>, other return <b>false</b>
     */
    @JsonIgnore
    public boolean isMain() {
        if (id == null || mainQuestionId == null) {
            throw new IllegalStateException();
        }
        return Objects.equals(id, mainQuestionId);
    }

    @JsonIgnore
    public boolean isSub() {
        return !this.isMain();
    }

    public QuestionMeta(TemplateQuestion templateQuestion) {
        this.contentEditable = templateQuestion.isEditable();
        this.setAnswerRequired(templateQuestion.isAnswerRequired());
    }

    public QuestionMeta(QuestionMeta questionMeta) {
        this.contentEditable = questionMeta.isContentEditable();
        this.setAnswerRequired(questionMeta.isAnswerRequired());
    }

    @Override
    public QuestionMeta clone() {
        try {
            return (QuestionMeta) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new RuntimeException(exception);
        }
    }
}