package org.veritas.assessment.biz.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class PlotFetchDto {
    @NotNull
    private Long modelArtifactVid;

    /**
     * <img id="" class="" src="">
     */
    private String imgId;

    private String imgClass;

    private String imgSrc;
}
