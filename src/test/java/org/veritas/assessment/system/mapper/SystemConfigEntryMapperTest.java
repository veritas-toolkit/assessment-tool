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

package org.veritas.assessment.system.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.system.constant.ConfigPropertyKeyEnum;
import org.veritas.assessment.system.entity.SystemConfigEntry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class SystemConfigEntryMapperTest {

    @Autowired
    private SystemConfigEntryMapper mapper;

    @Test
    void testLoadAllList_success() {
        assertNotNull(mapper);
        List<SystemConfigEntry> list = mapper.loadAllList();
        log.info("list:\n{}", list);
    }

    @Test
    void testUpdate_success() {
        SystemConfigEntry entry = new SystemConfigEntry();
        entry.setKey(ConfigPropertyKeyEnum.DEFAULT_EMAIL_SUFFIX);
        entry.setValue("@test_example.com");
        int count = mapper.update(entry);
        log.info("update rows count: {}", count);
    }
}