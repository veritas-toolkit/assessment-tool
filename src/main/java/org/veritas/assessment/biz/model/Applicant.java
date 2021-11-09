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

package org.veritas.assessment.biz.model;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

// /fairness_init/special_params/base_default_rate
// /fairness_init/special_params/num_applicants
@Slf4j
@Setter
@Getter
public class Applicant {
    private static final String KEY_NUM_APPLICANTS = "num_applicants";
    private static final String KEY_BASE_DEFAULT_RATE = "base_default_rate";
    private String feature;
    private List<?> numberList;
    private List<?> rateList;

    public static Map<String, Applicant> parseFromSpecialParams(Map<String, Object> specialParams) {
        if (specialParams == null || specialParams.isEmpty()) {
            return Collections.emptyMap();
        }
        Object numObject = specialParams.get(KEY_NUM_APPLICANTS);
        Object rateObject = specialParams.get(KEY_BASE_DEFAULT_RATE);
        if (Objects.isNull(numObject) || Objects.isNull(rateObject)) {
            return Collections.emptyMap();
        }
        Map<?, ?> numMap = null;
        if (numObject instanceof Map) {
            numMap = (Map<?, ?>) numObject;
        } else {
            return Collections.emptyMap();
        }
        Map<?, ?> rateMap = null;
        if (rateObject instanceof Map) {
            rateMap = (Map<?, ?>) rateObject;
        } else {
            return Collections.emptyMap();
        }
        Map<String, Applicant> map = new LinkedHashMap<>();
        for (Map.Entry<?, ?> entry : numMap.entrySet()) {
            Object key = entry.getKey();
            Object numValue = entry.getValue();
            Object rateValue = rateMap.get(key);
            if (numValue instanceof List && ((List<?>) numValue).size() >= 2) {
                if (rateValue instanceof List && ((List<?>) rateValue).size() >= 2) {
                    Applicant applicant = new Applicant();
                    applicant.setFeature(key.toString());
                    applicant.setNumberList((List<?>) numValue);
                    applicant.setRateList((List<?>) rateValue);
                    map.put(applicant.getFeature(), applicant);
                }
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
