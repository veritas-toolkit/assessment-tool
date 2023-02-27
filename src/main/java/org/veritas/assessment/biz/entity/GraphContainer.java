/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.biz.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Data
public class GraphContainer {

    private String correlationHeatMapChart;

    private String classDistributionPieChart;

    private String calibrationCurveLineChart;

    private String performanceLineChart;

    private String weightedConfusionHeatMapChart;

    private Map<String, String> featureDistributionPieChartMap = new LinkedHashMap<>();

    private Map<String, String> featureTradeoffContourMap = new LinkedHashMap<>();

    private Map<String, String> summaryPlotMap = new LinkedHashMap<>();

    private Map<String, Map<String, String>> partialDependencePlotMap = new LinkedHashMap<>();

    private Map<String, Map<String, String>> waterfallMap = new LinkedHashMap<>();

    public Map<String, String> getFeatureDistributionPieChartMap() {
        return Collections.unmodifiableMap(featureDistributionPieChartMap);
    }

    public void putFeatureDistributionPieChart(String feature, String chart) {
        if (StringUtils.isEmpty(feature)) {
            throw new IllegalArgumentException("Argument 'feature' should not be empty.");
        }
        String old = featureDistributionPieChartMap.put(feature, chart);
        if (old != null) {
            log.info("Feature[{}] update distribution chart.", feature);
        }
    }

    public void putSummaryPlot(String modelId, String plotImagePath) {
        if (modelId == null) {
            throw new IllegalArgumentException("Argument 'modelId' should not be empty.");
        }
        String old = this.summaryPlotMap.put(modelId, plotImagePath);
        if (old != null) {
            log.info("model[{}] summary plot updated from [{}] to [{}].", modelId, old, plotImagePath);
        }
    }

    public void putPartialDependencePlot(String modelId, String featureName, String plotImagePath) {
        if (modelId == null) {
            throw new IllegalArgumentException("Argument 'modelId' should not be empty.");
        }
        Map<String, String> featureMap = this.partialDependencePlotMap.computeIfAbsent(modelId,
                k -> new LinkedHashMap<>());

        String old = featureMap.put(featureName, plotImagePath);
        if (old != null) {
            log.info("model[{}] summary plot updated from [{}] to [{}].", modelId, old, plotImagePath);
        }
    }

    public void putWaterfall(String imgPath) {
        String basename = FilenameUtils.getBaseName(imgPath);
        final String SPIT = "_";
        String[] subList = basename.split(SPIT);
        if (subList.length >= 2) {
            String modelId = subList[subList.length - 2];
            String localId = subList[subList.length - 1];
            Map<String, String> map = this.waterfallMap.computeIfAbsent(modelId, k -> new LinkedHashMap<>());
            String old = map.put(localId, imgPath);
            if (old != null) {
                log.info("model[{}] summary plot updated from [{}] to [{}].", modelId, old, imgPath);
            }
        }
    }

    public List<String> waterfallPlotList() {
        List<String> list = new ArrayList<>();
        waterfallMap.values().forEach(e -> list.addAll(e.values()));
        return list;
    }


    public List<String> getModelIdList() {
        return new ArrayList<>(this.summaryPlotMap.keySet());
    }

    public List<String> getSummaryPlotList() {
        return new ArrayList<>(this.summaryPlotMap.values());
    }

    public String getSummaryPlot(String modelId) {
        return this.summaryPlotMap.get(modelId);
    }

    public List<String> getPartialDependenceFeatureList(String modelId) {
        Map<String, String> map = this.partialDependencePlotMap.get(modelId);
        if (map == null || map.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(map.keySet());
    }

    public String getPartialDependencePlot(String modelId, String feature) {
        Map<String, String> map = this.partialDependencePlotMap.get(modelId);
        if (map == null || map.isEmpty()) {
            return null;
        }
        return map.get(feature);
    }

    public List<String> getPartialDependencePlotList() {
        List<String> list = new ArrayList<>();
        for (Map<String, String> map : this.getPartialDependencePlotMap().values()) {
            list.addAll(map.values());
        }
        return list;
    }


    public String getFeatureDistributionPieChart(String feature) {
        if (StringUtils.isEmpty(feature)) {
            throw new IllegalArgumentException("Argument 'feature' should not be empty.");
        }
        String chart = featureDistributionPieChartMap.get(feature);
        if (chart == null) {
            log.warn("Feature[{}} has not distribution chart.", feature);
        }
        return chart;
    }


    public String findFeatureTradeoffContour(String feature) {
        String chart = featureTradeoffContourMap.get(feature);
        if (StringUtils.isEmpty(chart)) {
            log.warn("Feature[{}} has not trade off contour chart.", feature);
        }
        return chart;
    }

    public void putFeatureTradeoffContour(String feature, String imageBase64) {
        featureTradeoffContourMap.put(feature, imageBase64);
    }

    // FIXME: 2023/2/21 just for test
    public String getPersonalAttributesClassification() {
        List<String> list = new ArrayList<>(featureDistributionPieChartMap.values());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return classDistributionPieChart;
        }

    }

    // FIXME: 2023/2/21 just for test
    public String getPersonalAttributesIdentificationTree() {
        List<String> list = new ArrayList<>(featureDistributionPieChartMap.values());
        if (!list.isEmpty()) {
            return list.get(0);
        } else {
            return classDistributionPieChart;
        }
    }
}
