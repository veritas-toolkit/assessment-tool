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
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
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

    private Map<String, String> partial_dependence_plot = new LinkedHashMap<>();

    private String summary_plot;

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
        return "xxxxxxx.jpg";
//        return null;
    }
    public String getPersonalAttributesIdentificationTree() {
        return "yyyy.jpg";
//        return null;
    }
}
