package org.veritas.assessment.biz.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

public enum AssessmentStep {
    STEP_0(0, "Principles to Practice"),
    STEP_1(1, "Defining System Context and Design"),
    STEP_2(2, "Prepare and Input Data"),
    STEP_3(3, "Build and Validate"),
    STEP_4(4, "Deploy and Monitor");

    /**
     * The ordinal number of assessment step.
     */
    @Getter
    @EnumValue
    private final int stepSerial;

    @Getter
    private final String description;

    AssessmentStep(int stepSerial, String description) {
        this.stepSerial = stepSerial;
        this.description = description;
    }
}
