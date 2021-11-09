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

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.Group;

@Repository
@CacheConfig(cacheNames = "group")
public class GroupDao {
    public Pageable<Group> findGroupListPageable(int page, int pageSize, int uid, String prefix) {
        return null;
    }


}
