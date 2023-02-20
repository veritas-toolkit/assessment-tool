package org.veritas.assessment.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PlotFetchDto {
    @NotNull
    private Integer modelArtifactVid;

    /**
     * <img id="" class="" src="">
     */
    @NotEmpty
    private String imgId;

    private String imgClass;

    @NotEmpty
    private String imgSrc;
}
