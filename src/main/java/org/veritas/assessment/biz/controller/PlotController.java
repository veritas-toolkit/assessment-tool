package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.Loader;
import org.springframework.http.HttpEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}")
public class PlotController {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SimpleBarDto {
        private String type;
        private Map<String, BigDecimal> data;
    }

    @Operation(summary = "Fetch simple bar data")
    @GetMapping(path = "/plot")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public HttpEntity<?> plot(@PathVariable("projectId") int projectId,
                              @RequestParam(name = "questionnaireVid", required = false) Long questionnaireVid,
                              @RequestParam("plot-id") String plotId) {
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
