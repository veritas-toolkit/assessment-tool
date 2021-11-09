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

package org.veritas.assessment.system.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum ResourceType {
    PROJECT(1, "project"),
    GROUP(2, "group");

    @EnumValue
    private final int code;
    private final String typeName;

    ResourceType(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public static ResourceType ofName(String name) {
        for (ResourceType resourceType : values()) {
            if (StringUtils.equals(resourceType.getTypeName(), name)) {
                return resourceType;
            }
        }
        return null;
    }
}
