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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum ConfigPropertyKeyEnum {
    DEFAULT_EMAIL_SUFFIX("default_email_suffix", ConfigTypeEnum.DEFAULT_VALUE),
    DEFAULT_GROUP_LIMIT("default_limit_group", ConfigTypeEnum.DEFAULT_VALUE),
    DEFAULT_PROJECT_LIMIT("default_limit_project", ConfigTypeEnum.DEFAULT_VALUE),
    DEFAULT_USER_PASSWORD("default_user_password", ConfigTypeEnum.DEFAULT_VALUE),
    REGISTER_SUPPORTED("register_supported", ConfigTypeEnum.CONFIG),
    MODIFY_ACCOUNT_SUPPORTED("modify_account_supported", ConfigTypeEnum.CONFIG),
    ;

    @Getter
    @EnumValue
    private final String name;

    @Getter
    private final ConfigTypeEnum type;

    ConfigPropertyKeyEnum(String name, ConfigTypeEnum type) {
        this.name = name;
        this.type = type;
    }

    public static ConfigPropertyKeyEnum findByName(String name) {
        for (ConfigPropertyKeyEnum value : values()) {
            if (StringUtils.equals(value.getName(), name)) {
                return value;
            }
        }
        return null;
    }

    public static List<ConfigPropertyKeyEnum> findByType(ConfigTypeEnum configType) {
        return Arrays.stream(values())
                .filter(e -> e.type == configType)
                .collect(Collectors.toList());
    }
}
