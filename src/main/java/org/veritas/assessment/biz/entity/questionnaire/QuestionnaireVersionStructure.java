package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class QuestionnaireVersionStructure implements Comparable<QuestionnaireVersionStructure> {
    /**
     * questionnaire version id.
     */
    private int questionnaireVid;

    private int projectId;

    private int questionId;

    /**
     * question version id
     */
    private int questionVid;

    /**
     * If current question is a main-question then equal to {@link #questionVid}.
     */
    private int mainQuestionVid;

    private Principle principle;

    /**
     * Step serial, range from 0 to 4 in database.
     */
    private AssessmentStep step;

    /**
     * Serial no of principle.
     * <br/>
     * Example: For the first question of Fairness principle <b>F1</b>,
     * this serialOfPrinciple will be set as <b>1</b>.
     */
    private int serialOfPrinciple;

    /**
     * Sub-question serial of main question.
     * If current question is a main question then set as 0.
     */
    private int subSerial;

    @TableField(exist = false)
    private QuestionVersion questionVersion;

    @TableField(exist = false)
    private List<QuestionnaireVersionStructure> subList;

    public List<QuestionnaireVersionStructure> getSubList() {
        if (!this.isMain()) {
            throw new IllegalStateException("Sub question has no sub-question list.");
        }
        return subList;
    }

    public void setSubList(List<QuestionnaireVersionStructure> subList) {
        if (subList == null || subList.isEmpty()) {
            this.subList = Collections.emptyList();
        } else {
            this.subList = subList.stream()
                    .filter(e -> this.getQuestionnaireVid() == e.getQuestionnaireVid())
                    .filter(e -> this.getMainQuestionVid() == e.getMainQuestionVid())
                    .filter(e -> !e.isMain())
                    .sorted()
                    .collect(Collectors.toList());
        }
    }


    @Override
    public int compareTo(QuestionnaireVersionStructure o) {
        boolean sameProject = this.getProjectId() == o.getProjectId();
        boolean sameQuestionnaireVersion = this.getQuestionnaireVid() == o.getQuestionnaireVid();
        boolean samePrinciple = this.getPrinciple() == this.getPrinciple();
        if (!sameProject || !sameQuestionnaireVersion || !samePrinciple) {
            throw new IllegalArgumentException();
        }
        if (this.getSerialOfPrinciple() != o.getSerialOfPrinciple()) {
            return this.getSerialOfPrinciple() - o.getSerialOfPrinciple();
        } else {
            return this.getSubSerial() - o.getSubSerial();
        }
    }

    public boolean isMain() {
        return this.getQuestionVid() == this.getMainQuestionVid();
    }

    // 判断 QuestionVersion 是否符合当前
}
