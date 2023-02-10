package org.veritas.assessment.biz.constant;

import lombok.Getter;

public enum QuestionEditActionType {
    UNMODIFIED("unmodified");
    // input answer
    // edit sub-question content.
    // edit main-question content.
    // delete

    @Getter
    private final String action;

    QuestionEditActionType(String action) {
        this.action = action;
    }
}
