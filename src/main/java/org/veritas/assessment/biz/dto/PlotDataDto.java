package org.veritas.assessment.biz.dto;

import lombok.Data;
import org.veritas.assessment.biz.constant.PlotTypeEnum;

@Data
public class PlotDataDto {
    private PlotTypeEnum type;

    private String name;

    private String caption;

    private Object data;

    public static PlotDataDto none() {
        PlotDataDto plotDataDto = new PlotDataDto();
        plotDataDto.setType(PlotTypeEnum.NONE);
        plotDataDto.setName("none");
        plotDataDto.setData(null);
        plotDataDto.setCaption("None");
        return plotDataDto;
    }
}
