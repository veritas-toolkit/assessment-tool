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
import org.veritas.assessment.system.rbac.Permission;
import org.veritas.assessment.system.rbac.Role;

import java.util.Collections;
import java.util.List;

@Repository
@CacheConfig(cacheNames = "role")
@Slf4j
public class RoleDao {
    @Autowired
    private RoleMapper roleMapper;
    @Autowired
    private PermissionMapper permissionMapper;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Cacheable(key = "'id_'+#id", unless = "#result==null")
    public Role findById(int id) {
        Role role = roleMapper.selectById(id);
        if (role == null) {
            return null;
        }
        fixRole(role);
        return role;
    }

    @Cacheable(key = "'type_'+#type+'_name_'+#name", unless = "#result==null")
    public Role find(String type, String name) {
        Role role = roleMapper.findByTypeAndName(type, name);
        if (role == null) {
            return null;
        }
        fixRole(role);
        return role;
    }

    private void fixRole(Role role) {
        List<Permission> authList = permissionMapper.selectByRoleId(role.getId());
        authList = Collections.unmodifiableList(authList);
        role.setPermissionList(authList);

    }


}
