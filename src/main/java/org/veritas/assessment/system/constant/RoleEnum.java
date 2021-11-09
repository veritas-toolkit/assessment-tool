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

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public enum RoleEnum {
    PROJECT_OWNER(101, "project", "owner"),
    PROJECT_DEVELOPER(102, "project", "developer"),
    PROJECT_ASSESSOR(103, "project", "assessor"),

    GROUP_OWNER(301, "group", "owner"),
    GROUP_DEVELOPER(302, "group", "developer"),
    GROUP_ASSESSOR(303, "group", "assessor"),
    ;

    private final int id;
    private final String type;
    private final String name;

    RoleEnum(int id, String type, String name) {
        this.id = id;
        this.type = type;
        this.name = name;
    }

    public static RoleEnum get(String resourceType, RoleType roleType) {
        for (RoleEnum roleEnum : values()) {
            if (StringUtils.equalsIgnoreCase(resourceType, roleEnum.type) &&
                    StringUtils.equalsIgnoreCase(roleType.name(), roleEnum.getName())) {
                return roleEnum;
            }
        }
        return null;
    }
}
