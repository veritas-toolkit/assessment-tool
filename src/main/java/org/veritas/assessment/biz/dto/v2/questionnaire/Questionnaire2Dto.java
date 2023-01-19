package org.veritas.assessment.biz.dto.v2.questionnaire;

import lombok.Data;
import org.veritas.assessment.biz.constant.AssessmentStep;

import java.util.List;

// 问卷（目录） 仅仅包含到主问题，不含子问题及答案

// 问题 包含子问题及答案

// Echarts 需要的数据接口。
@Data
public class Questionnaire2Dto {


    /**
     * - principle
     *      - step [0-4]
     *          - question *
     */

    public class SubQuestionnaire {
        private AssessmentStep step;
        private List<Question2Dto> questionList;
    }
}