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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.system.constant.ConfigPropertyKeyEnum;
import org.veritas.assessment.system.service.SystemConfigService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class SystemConfigServiceImplTest {
    @Autowired
    private SystemConfigService service;


    @Test
    void testGetDefaultValue_success() {
        assertNotNull(service);
        Map<String, String> configProperties = service.findAllUserDefaultProperties();
        assertNotEquals(0, configProperties.size());
        log.info("all config:\n{}", configProperties);

        configProperties.keySet().forEach(key -> {
            assertTrue(StringUtils.startsWithIgnoreCase(key, "default"));
        });
    }

    @Test
    void name() {
        Map<String, String> map = service.findAllConfigProperties();
        log.info("config:\n{}", map);
    }

    @Test
    void test_success() {
        boolean result = service.registerSupported();
        assertFalse(result);
        service.updateConfig(ConfigPropertyKeyEnum.REGISTER_SUPPORTED, "true");
        result = service.registerSupported();
        assertTrue(result);

        service.updateConfig(ConfigPropertyKeyEnum.REGISTER_SUPPORTED, "1");
        result = service.registerSupported();
        assertTrue(result);
    }
}