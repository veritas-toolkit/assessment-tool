package org.veritas.assessment.biz.dto;

import lombok.Data;
import org.veritas.assessment.biz.constant.PlotTypeEnum;

@Data
public class PlotDataDto {
    private PlotTypeEnum type;

    private String name;

    private String caption;

    private Object data;
}
