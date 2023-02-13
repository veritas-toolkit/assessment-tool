package org.veritas.assessment.biz.constant;

import lombok.Getter;

public enum QuestionContentEditType {
    UNMODIFIED("unmodified"),
    ADD("add"),
    DELETE("delete"),
    MODIFIED("modify"),
    ;

    @Getter
    private final String action;

    QuestionContentEditType(String action) {
        this.action = action;
    }
}
