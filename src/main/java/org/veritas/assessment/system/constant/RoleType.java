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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public enum RoleType {
    OWNER(1, "owner"),
    DEVELOPER(2, "developer"),
    ASSESSOR(3, "assessor"),
    ;

    @Getter
    @EnumValue
    private final int code;

    @Getter
    private final String typeName;

    RoleType(int code, String typeName) {
        this.code = code;
        this.typeName = typeName;
    }

    public static RoleType fromName(String name) {
        for (RoleType type : values()) {
            if (StringUtils.equalsIgnoreCase(type.name(), name)) {
                return type;
            }
        }
        log.warn("MembershipType does not have called [{}} value.", name);
        return null;
    }

}
