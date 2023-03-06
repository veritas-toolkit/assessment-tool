package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.math.BigDecimal;
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

        @JsonProperty(value = "summary_plot")
        private String summaryPlot;

        @JsonProperty(value = "summary_plot_data_table")
        private List<SummaryPlotFeature> summaryPlotFeatureList;

        public byte[] getSummaryPlotImage() {
            return Base64.decodeBase64(this.summaryPlot);
        }

        public Map<String, byte[]> partialDependencePlotMap() {
            if (this.partialDependencePlot == null || partialDependencePlot.isEmpty()) {
                return Collections.emptyMap();
            }
            Map<String, byte[]> map = new LinkedHashMap<>();
            partialDependencePlot.forEach((k, v) -> map.put(k, Base64.decodeBase64(v)));
            return map;
        }


    }

    @Data
    public static class SummaryPlotFeature {
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
