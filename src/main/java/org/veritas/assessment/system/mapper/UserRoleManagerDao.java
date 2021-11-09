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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.rbac.UserRoleManager;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "userRoleManager")
@Slf4j
public class UserRoleManagerDao {

    @Autowired
    private UserRoleDao userRoleDao;

    @Cacheable(key = "#userId", unless = "#result==null")
    public UserRoleManager findById(Integer userId) {
        List<UserRole> userRoleList = userRoleDao.findAll(userId);
        return new UserRoleManager(userRoleList);
    }
}
