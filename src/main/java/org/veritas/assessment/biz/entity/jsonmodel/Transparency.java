package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class Transparency {
    @JsonProperty(value = "permutation")
    Permutation permutation;

    @JsonProperty(value = "model_list")
    List<ModelInfo> modelList;


    @Data
    public static class Permutation {
        private String title;
        private String footnote;
        List<PermutationScore> score;
    }

    @Data
    public static class PermutationScore {
        private String feature;
        private BigDecimal score;
    }

    @Data
    public static class ModelInfo {
        private Integer id;

        @JsonProperty(value = "local_interpretability")
        private List<LocalInterpretability> localInterpretabilityList;

        @JsonProperty(value = "partial_dependence_plot")
        private Map<String, String> partialDependencePlot;

        //        @JsonProperty(value = "summary_plot")
        private String summaryPlot;
        @JsonProperty(value = "summary_plot")
        private SummaryPlot summaryPlot2;

        public byte[] getSummaryPlotImage() {
            return Base64.decodeBase64(this.summaryPlot);
        }

        public Map<String, byte[]> partial_dependence_plot() {
            if (this.partialDependencePlot == null || partialDependencePlot.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, byte[]> map = new LinkedHashMap<>();
            partialDependencePlot.forEach((k, v) -> map.put(k, Base64.decodeBase64(v)));
            return map;
        }


    }

    @Data
    public static class SummaryPlot {
        @JsonProperty("values")
        private Object values;
        private String plotImageBase64;
        List<SummaryPlotElement> summaryPlotElementList;
        @JsonProperty("plot_display")
        private Boolean plotDisplay;


        public void test() {
            if (values == null) {
                return;
            }
            log.debug("values type: {}", values.getClass().getName());
            if (values instanceof String) {
                log.debug("summary plot base64");
                this.plotImageBase64 = (String) values;
            } else if (values instanceof List) {
                log.debug("summary plot data");
                if (this.summaryPlotElementList != null) {
                    this.summaryPlotElementList = new ArrayList<>();
                }
                ((List<?>) values).forEach(e -> {
                    if (e instanceof Map) {
                        SummaryPlotElement element = new SummaryPlotElement();
                        Object feature = ((Map<?, ?>) e).get("Feature_name");
                        if (feature instanceof String) {
                            element.setFeature((String) feature);
                        }
                        Object meanShap = ((Map<?, ?>) e).get("Mean|shap|");
                        if (meanShap instanceof BigDecimal) {
                            element.setMeanOfAbsoluteValueOfShap((BigDecimal) meanShap);
                        }
                        this.summaryPlotElementList.add(element);
                    }
                });
            } else {
                log.warn("Cannot recognize the values: {}", values);
            }
        }
    }

    @Data
    public static class SummaryPlotElement {
        @JsonProperty("Feature_name")
        private String feature;
        @JsonProperty("Mean|shap|")
        private BigDecimal meanOfAbsoluteValueOfShap;
    }

    @Data
    public static class LocalInterpretability {
        private Integer id;
        private BigDecimal efx;
        private BigDecimal fx;
        @JsonProperty("plot_display")
        private Boolean plotDisplay;
        @JsonProperty(value = "feature_info")
        private List<FeatureInfo> featureInfoList;
    }

    @Data
    public static class FeatureInfo {
        @JsonProperty(value = "Feature_name")
        private String name;
        @JsonProperty(value = "Value")
        private BigDecimal value;
        @JsonProperty(value = "Shap")
        private BigDecimal shap;
    }


}
