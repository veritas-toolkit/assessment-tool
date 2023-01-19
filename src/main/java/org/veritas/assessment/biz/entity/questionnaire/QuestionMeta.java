package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * Save Question metadata.
 * Every question, including main-question and sub-question, only has one record.
 */
@Data
public class QuestionMeta {
    @TableId(type = IdType.AUTO)
    private int id;

    /**
     * Point to the current version id of the question.
     * <br/>
     * This property can be used as the LOCK to add a new {@link QuestionVersion} record.
     */
    private int currentVid;

    private int projectId;
    private int mainQuestionId;

    private boolean editable = DEFAULT_EDITABLE;

    /**
     * Add this question the questionnaire from this questionnaire version.
     */
    private int addStartQuestionnaireVid;

    /**
     * If the question be deleted
     */
    private Integer deleteStartQuestionnaireVid;

    private static final boolean DEFAULT_EDITABLE = false;

    /**
     * Check if current question the main question.
     * @return main question return <b>true</b>, other return <b>false</b>
     */
    public boolean isMain() {
        return id == mainQuestionId;
    }

}