package org.veritas.assessment.biz.constant;

public enum PlotTypeEnum {
    NONE("none", "Not need to transform as plot."),
    PIE("pie", "Pie"),
    BAR("bar", "Bar"),
    CURVE("curve", "Curve"),
    ;

    private final String name;
    private final String description;

    PlotTypeEnum(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
