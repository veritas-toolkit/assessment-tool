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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.system.entity.SystemConfigEntry;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "system_config_entry")
public interface SystemConfigEntryMapper extends BaseMapper<SystemConfigEntry> {

    @CacheEvict(allEntries = true)
    default int add(SystemConfigEntry systemConfigEntry) {
        return insert(systemConfigEntry);
    }

    @CacheEvict(allEntries = true)
    default int update(SystemConfigEntry systemConfigEntry) {
        int result = updateById(systemConfigEntry);
        if (result < 1) {
            result = insert(systemConfigEntry);
        }
        return result;
    }

    @Cacheable(key = "'all'")
    default List<SystemConfigEntry> loadAllList() {
        LambdaQueryWrapper<SystemConfigEntry> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SystemConfigEntry::getKey);
        return selectList(null);
    }
}
