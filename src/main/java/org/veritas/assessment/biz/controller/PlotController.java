package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.constant.PlotTypeEnum;
import org.veritas.assessment.biz.dto.PlotDataDto;
import org.veritas.assessment.biz.dto.PlotFetchDto;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/")
public class PlotController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleBarDto {
        private String type;
        private Map<String, BigDecimal> data;
    }



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

    ////////////////////////////////

    @Operation(summary = "Fetch simple bar data")
    @GetMapping(path = "/plot")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public HttpEntity<?> plot(@PathVariable("projectId") int projectId,
                              @RequestParam(name = "questionnaireVid", required = false) Long questionnaireVid,
                              @RequestParam("plot-id") String plotId) throws IOException {
        if (questionnaireVid == null) {
            // fetch the latest questionnaire
        }
        switch (plotId) {
            case "plot-bar":
                return new HttpEntity<>(this.simpleBarDto(projectId));
            default:
                return null;
        }
    }



    @Operation(summary = "Fetch simple bar data")
    @GetMapping(path = "/plot-bar/", params = {"plot-type=test"})
    public SimpleBarDto simpleBarDto(@PathVariable("projectId") int projectId) {
        Map<String, BigDecimal> data = new LinkedHashMap<>();
        data.put("Mon", new BigDecimal(120));
        data.put("Tue", new BigDecimal(200));
        data.put("Wed", new BigDecimal(150));
        data.put("Thu", new BigDecimal(80));
        data.put("Fri", new BigDecimal(70));
        data.put("Sat", new BigDecimal(110));
        data.put("Sun", new BigDecimal(130));
        SimpleBarDto dto = new SimpleBarDto("bar", data);
        return dto;
    }

    @Operation(summary = "Fetch simple bar data")
    @GetMapping(path = "/confusionmatrix/", params = {"plot-type=confusion-matrix"})
    public HeatmapDto heatmapDto() {
        return null;
    }

    public static class HeatmapDto {

    }








}
