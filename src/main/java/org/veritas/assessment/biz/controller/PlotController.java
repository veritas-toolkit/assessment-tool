package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.dto.PlotDataDto;
import org.veritas.assessment.biz.dto.PlotFetchDto;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/")
public class PlotController {


    @Autowired
    private ModelArtifactService modelArtifactService;

    @Operation(summary = "Fetch data to generate echarts plot.")
    @PostMapping(path = "/plot")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public PlotDataDto fetchPlotData(@Parameter(hidden = true) User operator,
                                     @PathVariable("projectId") Integer projectId,
                                     @Valid @RequestBody PlotFetchDto plotFetchDto) {
        try {
            ModelArtifact modelArtifact = modelArtifactService.findByVersionId(plotFetchDto.getModelArtifactVid());
            if (modelArtifact == null) {
                return PlotDataDto.none();
            }
            try {
                modelArtifactService.loadContent(modelArtifact);
            } catch (IOException e) {
                log.warn("load model artifact failed", e);
                return PlotDataDto.none();
            }
            return modelArtifactService.findPlotData(modelArtifact,
                    plotFetchDto.getImgId(), plotFetchDto.getImgClass(), plotFetchDto.getImgSrc());
        } catch (Exception exception) {
            log.error("fetch plot failed.project [{}], param:{}", projectId, plotFetchDto, exception);
            return PlotDataDto.none();
        }
    }

}
