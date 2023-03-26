package org.veritas.assessment.biz.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

public enum Principle {
    G("G", "Generic"),
    F("F", "Fairness"),
    EA("EA", "Ethics & Accountability"),
    T("T", "Transparency"),
    ;

    @EnumValue
    @Getter
    @JsonValue
    private final String shortName;
    @Getter
    private final String fullname;

    Principle(String name, String fullname) {
        this.shortName = name;
        this.fullname = fullname;
    }
}
