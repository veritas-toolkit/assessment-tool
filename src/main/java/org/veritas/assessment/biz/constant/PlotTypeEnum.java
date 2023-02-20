package org.veritas.assessment.biz.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum PlotTypeEnum {
    NONE("none", "Not need to transform as plot."),
    PIE("pie", "Pie"),
    BAR("bar", "Bar"),
    CURVE("curve", "Curve"),
    TWO_LINE("two_line", "Two line"),
    ;

    @JsonValue
    private final String name;
    private final String description;

    PlotTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
