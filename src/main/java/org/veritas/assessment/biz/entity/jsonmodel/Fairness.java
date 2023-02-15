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

package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Data
@Slf4j
public class Fairness {
    private final int CLASS_DISTRIBUTION_MAX_MIN_TIMES = 2;
    @JsonProperty(value = "fairness_init")
    private FairnessInit fairnessInit;
    @JsonProperty(value = "class_distribution")
    private Map<String, BigDecimal> classDistribution;
    @JsonProperty(value = "weighted_confusion_matrix")
    private WeightedConfusionMatrix weightedConfusionMatrix;

    @JsonProperty(value = "perf_metric_values")
    private Map<String, List<BigDecimal>> perfMetricValues;

    @JsonProperty(value = "perf_dynamic")
    private PerfDynamic perfDynamic;

    @JsonProperty(value = "calibration_curve")
    private CalibrationCurve calibrationCurve;
    @JsonProperty(value = "correlation_matrix")
    private CorrelationMatrix correlationMatrix;
    @JsonProperty(value = "features")
    private Map<String, Feature> featureMap;

    @JsonProperty(value = "individual_fairness")
    private IndividualFairness individualFairness;


    public Feature getFeature(String featureName) {
        return featureMap.get(featureName);
    }

    public boolean negativeDistributionIsLarge() {
        if (classDistribution == null) {
            return false;
        }
        String negativeKeyWord = "neg";
        Optional<String> optional = classDistribution.keySet()
                .stream()
                .filter(key -> StringUtils.startsWithIgnoreCase(key, negativeKeyWord))
                .findFirst();
        if (!optional.isPresent()) {
            return false;
        }
        BigDecimal value = classDistribution.get(optional.get());
        return value.compareTo(new BigDecimal("0.4")) >= 0;
    }

    public boolean isPerfMetric(String feature) {
        if (fairnessInit == null || StringUtils.isEmpty(fairnessInit.perfMetricName)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(feature, fairnessInit.perfMetricName);
    }

    public boolean isFairMetric(String feature) {
        if (fairnessInit == null || StringUtils.isEmpty(fairnessInit.fairMetricName)) {
            return false;
        }
        return StringUtils.equalsIgnoreCase(feature, fairnessInit.fairMetricName);
    }

    public boolean hasTradeoff() {
        if (featureMap == null || featureMap.isEmpty()) {
            return false;
        }
        return featureMap.values().stream().anyMatch(e -> e.getTradeoff() != null);
    }

    // get the max
    public Pair<String, BigDecimal> maxClassDistributionName() {
        BigDecimal max = null;
        String name = null;
        for (Map.Entry<String, BigDecimal> entry : classDistribution.entrySet()) {
            BigDecimal value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (max == null || max.compareTo(value) < 0) {
                max = value;
                name = entry.getKey();
            }
        }
        if (name == null) {
            return null;
        } else {
            return Pair.of(name, max);
        }
    }

    //get min
    public Pair<String, BigDecimal> minClassDistributionName() {
        BigDecimal min = null;
        String name = null;
        for (Map.Entry<String, BigDecimal> entry : classDistribution.entrySet()) {
            BigDecimal value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (min == null || min.compareTo(value) > 0) {
                min = value;
                name = entry.getKey();
            }
        }
        if (name == null) {
            return null;
        } else {
            return Pair.of(name, min);
        }
    }

    // FIXME: 2021/9/16 change the function name to be more heshi
    public boolean classDistributionIsAverage() {
        if (classDistribution == null || classDistribution.isEmpty()) {
            return true;
        }
        BigDecimal min = null;
        BigDecimal max = null;
        for (Map.Entry<String, BigDecimal> entry : classDistribution.entrySet()) {
            BigDecimal value = entry.getValue();
            if (value == null) {
                continue;
            }
            if (min == null || min.compareTo(value) > 0) {
                min = value;
            }
            if (max == null || max.compareTo(value) < 0) {
                max = value;
            }
        }
        if (min == null || max == null) {
            return true;
        }
        BigDecimal times = max.divide(min, 2, RoundingMode.HALF_UP);
        return times.compareTo(new BigDecimal(CLASS_DISTRIBUTION_MAX_MIN_TIMES)) <= 0;
    }

    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FairnessInit {
        @JsonProperty(value = "fair_metric_name_input")
        private String fairMetricNameInput;

        @JsonProperty(value = "fair_metric_name")
        private String fairMetricName;
        @JsonProperty(value = "perf_metric_name")
        private String perfMetricName;
        @JsonProperty(value = "protected_features")
        private List<String> protectedFeatureList;

        @JsonProperty(value = "fair_priority")
        private String fairPriority;

        @JsonProperty(value = "fair_concern")
        private String fairConcern;

        @JsonProperty(value = "fair_impact")
        private String fairImpact;

        // 'special_params' node may be different in different json file.
        @JsonProperty(value = "special_params")
        private Map<String, Object> specialParameters;

        @JsonProperty(value = "fair_threshold_input")
        private BigDecimal fairThresholdInput;

        @JsonProperty(value = "fair_neutral_tolerance")
        private BigDecimal fairNeutralTolerance;


        @JsonProperty(value = "num_rejected_applicants")
        private Map<String, List<BigDecimal>> numberOfRejectedApplicants;
        @JsonProperty(value = "base_default_rate")
        private Map<String, List<BigDecimal>> baseDefaultRate;

        public String fairConcernDisplay() {
            if (StringUtils.equalsIgnoreCase(fairConcern, "eligible")) {
                return "advantageous";
            } else if (StringUtils.equalsIgnoreCase(fairConcern, "inclusive")) {
                return "disadvantageous";
            } else if (StringUtils.equalsIgnoreCase(fairConcern, "both")) {
                return "both advantageous and disadvantageous";
            } else {
                return fairConcern != null ? fairConcern : "";
            }
        }
    }

    public static class SpecialParameter extends LinkedHashMap<String, List<BigDecimal>> {

    }

    @Data
    public static class WeightedConfusionMatrix {
        //  true positive
        private BigDecimal tp;
        // false positive
        private BigDecimal fp;
        // true negative
        private BigDecimal tn;
        // false negative
        private BigDecimal fn;

        public boolean hasNull() {
            return Objects.isNull(tp) || Objects.isNull(fp) || Objects.isNull(tn) || Objects.isNull(fn);
        }
    }

    @Data
    public static class CalibrationCurve {
        @JsonProperty(value = "prob_true")
        private List<BigDecimal> probTrue;

        @JsonProperty(value = "prob_pred")
        private List<BigDecimal> probPredicted;

        @JsonProperty(value = "score")
        private BigDecimal score;
    }

    @Data
    public static class PerfDynamic {
        @JsonProperty(value = "perf_metric_name")
        private String perfMetricName;

        @JsonProperty(value = "threshold")
        private List<BigDecimal> thresholdList;

        @JsonProperty(value = "perf")
        private List<BigDecimal> perfList;

        @JsonProperty(value = "selection_rate")
        private List<BigDecimal> selectionRateList;
    }

    @Data
    public static class CorrelationMatrix {
        @JsonProperty(value = "feature_names")
        private List<String> featureNameList;

        @JsonProperty(value = "corr_values")
        private List<List<BigDecimal>> correlationValues;
    }

    @Data
    @NoArgsConstructor
    public static class Feature {
        private final int DEFAULT_THRESHOLD = 2;
        @JsonProperty(value = "fair_threshold")
        private BigDecimal fairThreshold;
        @JsonProperty(value = "privileged")
//        private List<String> privilegeList;
        private List<List<Object>> privilegedList;
//        private List<List<Object>> privilegeList;
        @JsonProperty(value = "unprivileged")
        private List<List<Object>> unprivilegedList;

        @JsonProperty(value = "feature_distribution")
        private Map<String, BigDecimal> featureDistributionMap;
        @JsonProperty(value = "fair_metric_values")
        private Map<String, List<BigDecimal>> fairMetricValueListMap;
        @JsonProperty(value = "fairness_conclusion")
        private String fairnessConclusion;
        @JsonProperty(value = "tradeoff")
        private Tradeoff tradeoff;
        // list example: "gender": [-0.03, -0.04, "fair to fair", "include"]
        @JsonProperty(value = "feature_importance")
        private Map<String, List<Object>> featureImportance;

        public static String faireMetricValueFormat(List<BigDecimal> valueList) {
            BigDecimal value = BigDecimal.ZERO;
            BigDecimal range = BigDecimal.ZERO;
            if (valueList != null) {
                if (valueList.size() >= 1) {
                    value = valueList.get(0);
                }
                if (valueList.size() >= 2) {
                    range = valueList.get(1);
                }
            }
            DecimalFormat df = new DecimalFormat("#0.000");
            return String.format("%s +/- %s", df.format(value), df.format(range));
        }

        // call from ftl
        public BigDecimal distributionRatio() {
            BigDecimal min = null;
            BigDecimal max = null;
            for (Map.Entry<String, BigDecimal> entry : featureDistributionMap.entrySet()) {
                BigDecimal value = entry.getValue();
                if (min == null) {
                    min = value;
                } else if (min.compareTo(value) > 0) {
                    min = value;
                }
                if (max == null) {
                    max = value;
                } else if (max.compareTo(value) < 0) {
                    max = value;
                }
            }
            if (max == null || min == null || min.compareTo(BigDecimal.ZERO) == 0) {
                log.warn("");
                return BigDecimal.ZERO;
            }
            return max.divide(min, 4, RoundingMode.HALF_UP);

        }

        // call from ftl
        public boolean isLow() {
            return isLow(null);
        }

        // call from ftl
        public boolean isLow(BigDecimal threshold) {
            if (threshold == null) {
                threshold = BigDecimal.valueOf(DEFAULT_THRESHOLD);
            }
            BigDecimal ratio = distributionRatio();
            return ratio.compareTo(threshold) < 0;
        }

        public Optional<BigDecimal> findFairMetricValue(Fairness jsonModel) {
            if (fairMetricValueListMap == null || fairMetricValueListMap.isEmpty()) {
                return Optional.empty();
            }
            String name = jsonModel.fairnessInit.fairMetricName;
            if (name == null) {
                return Optional.empty();
            }
            List<BigDecimal> list = fairMetricValueListMap.get(name);
            if (list == null || list.isEmpty()) {
                return Optional.empty();
            }
            return Optional.of(list.get(0));

        }

        public Optional<List<Object>> findImportanceInfo(Fairness jsonModel) {
            if (this.getFeatureImportance() == null) {
                return Optional.empty();
            }

            Map<String, Feature> map = jsonModel.featureMap;
            String feature = null;
            for (Map.Entry<String, Feature> entry : map.entrySet()) {
                if (entry.getValue() == this) {
                    feature = entry.getKey();
                    break;
                }
            }
            assert feature != null;
            List<Object> list = this.getFeatureImportance().get(feature);
            if (list == null || list.size() < 4) {
                log.warn("");
                return Optional.empty();
            } else {
                return Optional.of(list);
            }
        }

        // fair to fair
        // fair to unfair
        // unfair to fair
        // unfair to unfair
        @JsonIgnore
        public boolean isToFair(String fairnessChangeDescription) {
            if (StringUtils.isEmpty(fairnessChangeDescription)) {
                return false;
            }
            return StringUtils.containsIgnoreCase(fairnessChangeDescription, "to fair");
        }

    }

    @Data
    public static class Tradeoff {
        @JsonProperty(value = "fair_metric_name")
        private String fairMetricName;
        @JsonProperty(value = "perf_metric_name")
        private String perfMetricName;

        @JsonProperty(value = "fair")
        private List<List<BigDecimal>> fair;

        @JsonProperty(value = "perf")
        private List<List<BigDecimal>> perf;

        @JsonProperty(value = "th_x")
        private List<BigDecimal> th_x;

        @JsonProperty(value = "th_y")
        private List<BigDecimal> th_y;

        @JsonProperty(value = "max_perf_point")
        private List<BigDecimal> max_perf_point;

        @JsonProperty(value = "max_perf_single_th")
        private List<BigDecimal> max_perf_single_th;

        @JsonProperty(value = "max_perf_neutral_fair")
        private List<BigDecimal> max_perf_neutral_fair;

    }

    @Data
    public static class IndividualFairness{
        @JsonProperty(value = "consistency_score")
        private BigDecimal consistencyScore;
    }

}
