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

package org.veritas.assessment.system.service;

import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.constant.ConfigPropertyKeyEnum;

import java.util.Map;

public interface SystemConfigService {

    Map<String, String> findAllConfigProperties();

    Map<String, String> findAllUserDefaultProperties();

    @Transactional
    default void updateConfig(String key, String value) throws ErrorParamException {
        ConfigPropertyKeyEnum keyEnum = ConfigPropertyKeyEnum.findByName(key);
        if (keyEnum == null) {
            throw new ErrorParamException(String.format("There is no named[%s] config entry.", key));
        } else {
            updateConfig(keyEnum, value);
        }
    }

    @Transactional
    void updateConfig(ConfigPropertyKeyEnum keyEnum, String value) throws ErrorParamException;

    boolean registerSupported();

    boolean modifyAccountSupported();

}
