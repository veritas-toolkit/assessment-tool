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

package org.veritas.assessment.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.constant.ConfigPropertyKeyEnum;
import org.veritas.assessment.system.constant.ConfigTypeEnum;
import org.veritas.assessment.system.entity.SystemConfigEntry;
import org.veritas.assessment.system.mapper.SystemConfigEntryMapper;
import org.veritas.assessment.system.service.SystemConfigService;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class SystemConfigServiceImpl implements SystemConfigService {

    @Autowired
    private SystemConfigEntryMapper mapper;

    @Override
    public Map<String, String> findAllConfigProperties() {
        List<SystemConfigEntry> list = mapper.loadAllList();
        Map<String, String> map = new LinkedHashMap<>();
        for (SystemConfigEntry systemConfigEntry : list) {
            ConfigPropertyKeyEnum key = systemConfigEntry.getKey();
            if (key != null && key.getType() == ConfigTypeEnum.CONFIG) {
                map.put(systemConfigEntry.getKey().getName(), systemConfigEntry.getValue());
            }
        }
        for (ConfigPropertyKeyEnum keyEnum : ConfigPropertyKeyEnum.findByType(ConfigTypeEnum.CONFIG)) {
            if (!map.containsKey(keyEnum.getName())) {
                map.put(keyEnum.getName(), null);
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    @Transactional
    public Map<String, String> findAllUserDefaultProperties() {
        List<SystemConfigEntry> list = mapper.loadAllList();
        Map<String, String> map = new LinkedHashMap<>();
        for (SystemConfigEntry systemConfigEntry : list) {
            ConfigPropertyKeyEnum key = systemConfigEntry.getKey();
            if (key != null && key.getType() == ConfigTypeEnum.DEFAULT_VALUE) {
                map.put(systemConfigEntry.getKey().getName(), systemConfigEntry.getValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }

    @Override
    @Transactional
    public void updateConfig(ConfigPropertyKeyEnum keyEnum, String value) throws ErrorParamException {
        Objects.requireNonNull(keyEnum);
        SystemConfigEntry entry = new SystemConfigEntry(keyEnum, value);
        int result = mapper.update(entry);
        if (result < 1) {
            log.warn("Update system config[{}] may failed.", keyEnum.getName());
        }
    }

    @Override
    public boolean registerSupported() {
        Optional<SystemConfigEntry> optional = findEntryByKey(ConfigPropertyKeyEnum.REGISTER_SUPPORTED);
        if (!optional.isPresent()) {
            return false;
        }
        SystemConfigEntry entry = optional.get();
        return entry.getBooleanValue();
    }

    @Override
    public boolean modifyAccountSupported() {
        Optional<SystemConfigEntry> optional = findEntryByKey(ConfigPropertyKeyEnum.MODIFY_ACCOUNT_SUPPORTED);
        if (!optional.isPresent()) {
            return false;
        }
        SystemConfigEntry entry = optional.get();
        return entry.getBooleanValue();
    }

    private Optional<SystemConfigEntry> findEntryByKey(ConfigPropertyKeyEnum key) {
        if (key == null) {
            return Optional.empty();
        }
        List<SystemConfigEntry> list = mapper.loadAllList();
        return list.stream()
                .filter(systemConfigEntry -> systemConfigEntry.getKey() == key)
                .findFirst();
    }
}
