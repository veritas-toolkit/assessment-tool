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
    @JsonProperty(value = "permutation_score")
    List<PermutationScore> permutationScoreList;

    @JsonProperty(value = "model_list")
    List<ModelInfo> modelList;




    @Data
    public static class ModelInfo {
        private Integer id;

        @JsonProperty(value = "local_interpretability")
        private List<LocalInterpretability> localInterpretabilityList;

        @JsonProperty(value = "partial_dependence_plot")
        private Map<String, String> partialDependencePlot;

        @JsonProperty(value = "summary_plot")
        private String summaryPlot;

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
    public static class LocalInterpretability {
        private String id;
        private BigDecimal efx;
        private BigDecimal fx;
        @JsonProperty(value = "feature_info")
        private List<FeatureInfo> featureInfoList;
    }

    @Data
    public static class FeatureInfo {
        private String name;
        private BigDecimal value;
        private BigDecimal shap;
    }

    @Data
    public static class PermutationScore {
        private String feature;

        private BigDecimal score;
    }
}
