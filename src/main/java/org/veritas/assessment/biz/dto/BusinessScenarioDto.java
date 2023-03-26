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

package org.veritas.assessment.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;

import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessScenarioDto {
    private Integer code;
    private String name;
    private String description;


    public BusinessScenarioDto(BusinessScenarioEnum businessScenario) {
        Objects.requireNonNull(businessScenario);
        this.code = businessScenario.getCode();
        this.name = businessScenario.getName();
        this.description = businessScenario.getDescription();
    }
}
