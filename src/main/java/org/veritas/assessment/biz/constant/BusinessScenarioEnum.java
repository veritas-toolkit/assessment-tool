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

package org.veritas.assessment.biz.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

@Getter
public enum BusinessScenarioEnum {
    BASE_CLASSIFICATION(1, "Base Classification", "Base Classification", "base_regression"),
    BASE_REGRESSION(2, "Base Regression", "Base Regression", "base_regression"),
    CREDIT_SCORING(10, "Credit Scoring", "Credit Scoring", "credit_scoring"),
    CUSTOMER_MARKETING(20, "Customer Marketing", "Customer Marketing", "customer_marketing"),
    PUW(30, "PUW", "PUW", "PUW"),
    ;
    @EnumValue
    private final Integer code;

    private final String name;

    private final String description;

    @JsonIgnore
    private final String answerTemplatePath;

    BusinessScenarioEnum(Integer code, String name, String description, String answerTemplatePath) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.answerTemplatePath = answerTemplatePath;
    }
}
