package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class Transparency {
    //    @JsonProperty(value = "permutation_score")
    private Map<String, BigDecimal> permutationScoreMap;

    private Map<String, ModelInfo> models = new LinkedHashMap<>();

    @JsonAnySetter
    public void set(String fieldName, ModelInfo value) {
        this.models.put(fieldName, value);
    }

    public Object get(String fieldName) {
        return this.models.get(fieldName);
    }

    @JsonSetter(value = "permutation_score")
    public void set(List<List<Object>> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        for (List<Object> objectList : list) {
            if (list.size() >= 2) {
                Object keyObject = objectList.get(0);
                Object valueObject = objectList.get(1);
                if (keyObject instanceof String && valueObject instanceof BigDecimal) {
                    map.put((String) keyObject, (BigDecimal) valueObject);
                }
            }
        }
        this.permutationScoreMap = map;


    }


    @Data
    public static class ModelInfo {
        //        @JsonProperty(value = "local_interpretability")
        private List<LocalInterpretability> localInterpretabilityList;

        @JsonProperty(value = "partial_dependence_plot")
        private Map<String, String> partialDependencePlot;
//        private Map<String, PlotContent> partialDependencePlot;

        @JsonProperty(value = "summary_plot")
        private String summaryPlot;
//        private PlotContent summaryPlot;

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

        @JsonSetter(value = "local_interpretability")
        public void set(Map<String, List<List<Object>>> map) {
            if (map == null || map.isEmpty()) {
                this.localInterpretabilityList = Collections.emptyList();
                return;
            }
            List<LocalInterpretability> list = new ArrayList<>();
            map.forEach((k, listValue) -> {
                LocalInterpretability interpretability = new LocalInterpretability();
                interpretability.setId(k);
                int length = listValue.size();
                if (length >= 1) {
                    List<Object> firstList = listValue.get(0);
                    if (firstList.size() >= 2) {
                        Object efxKey = firstList.get(0);
                        Object efxValue = firstList.get(1);
                        if (efxKey instanceof CharSequence) {
                            if (StringUtils.equals((CharSequence) efxKey, "efx")) {
                                if (efxValue instanceof BigDecimal) {
                                    interpretability.setEfx((BigDecimal) efxValue);
                                }
                            }
                        }
                    }
                }
                if (length >= 2) {
                    List<Object> secondList = listValue.get(1);
                    if (secondList.size() >= 2) {
                        Object fxKey = secondList.get(0);
                        Object fxValue = secondList.get(1);
                        if (fxKey instanceof CharSequence) {
                            if (StringUtils.equals((CharSequence) fxKey, "fx")) {
                                if (fxValue instanceof BigDecimal) {
                                    interpretability.setFx((BigDecimal) fxValue);
                                }
                            }
                        }
                    }
                }
                List<FeatureLocalInfo> featureLocalInfoList = new ArrayList<>(length);
                if (length >= 3) {
                    for (int i = 2; i < length; ++i) {
                        List<Object> objectList = listValue.get(i);
                        if (objectList.size() >= 3) {
                            Object featureObj = objectList.get(0);
                            Object valueObj = objectList.get(1);
                            Object shapObj = objectList.get(2);
                            if (featureObj instanceof String) {
                                if (valueObj instanceof BigDecimal && shapObj instanceof BigDecimal) {
                                    FeatureLocalInfo featureLocalInfo = new FeatureLocalInfo();
                                    featureLocalInfo.setName((String) featureObj);
                                    featureLocalInfo.setValue((BigDecimal) valueObj);
                                    featureLocalInfo.setShap((BigDecimal) shapObj);
                                    featureLocalInfoList.add(featureLocalInfo);
                                }
                            }
                        }
                    }
                }
                interpretability.setFeatureLocalInfoList(featureLocalInfoList);
                list.add(interpretability);
            });
            this.localInterpretabilityList = list;

        }

    }

    @Data
    public static class LocalInterpretability {
        private String id;
        private BigDecimal efx;
        private BigDecimal fx;
        private List<FeatureLocalInfo> featureLocalInfoList;
    }

    @Data
    public static class FeatureLocalInfo {
        private String name;
        private BigDecimal value;
        private BigDecimal shap;
    }

}
