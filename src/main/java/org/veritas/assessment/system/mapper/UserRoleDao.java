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

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.rbac.Role;
import org.veritas.assessment.system.rbac.UserRole;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
public class UserRoleDao {
    private static final String CACHE_NAME_U_R_MANAGER = "userRoleManager";
    private static final String CACHE_NAME_U_R_LIST_RESOURCE = "user_role_list_of_resource";
    @Autowired
    private RoleDao roleDao;
    @Autowired
    private UserRoleMapper userRoleMapper;

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_MANAGER, key = "#userRole.userId"),
                    @CacheEvict(
                            cacheNames = CACHE_NAME_U_R_LIST_RESOURCE,
                            key = "#userRole.resourceType.code + '_' + #userRole.resourceId"
                    )
            }
    )
    public UserRole add(UserRole userRole) {
        userRoleMapper.insert(userRole);
        return userRole;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_MANAGER, key = "#userRole.userId"),
                    @CacheEvict(
                            cacheNames = CACHE_NAME_U_R_LIST_RESOURCE,
                            key = "#userRole.resourceType.code + '_' + #userRole.resourceId"
                    )
            }
    )
    public int delete(UserRole userRole) {
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getUserId, userRole.getUserId());
        wrapper.eq(UserRole::getResourceId, userRole.getResourceId());
        wrapper.eq(UserRole::getResourceType, userRole.getResourceType());
        return userRoleMapper.delete(wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_MANAGER, key = "#userId"),
                    @CacheEvict(
                            cacheNames = CACHE_NAME_U_R_LIST_RESOURCE,
                            key = "#resourceType.code + '_' + #resourceId"
                    )
            }
    )
    public int delete(Integer userId, ResourceType resourceType, Integer resourceId) {
        LambdaUpdateWrapper<UserRole> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        wrapper.eq(UserRole::getResourceType, resourceType);
        wrapper.eq(UserRole::getResourceId, resourceId);
        return userRoleMapper.delete(wrapper);
    }

    public List<UserRole> findAll(int userId) {
        return this.findAllUserResourceType(userId, null);
    }

    public List<UserRole> findAllUserResourceType(int userId, ResourceType resourceType) {
        List<UserRole> userRoleList = userRoleMapper.findByUserIdResourceType(userId, resourceType);
        if (userRoleList == null || userRoleList.isEmpty()) {
            return Collections.emptyList();
        }
        userRoleList.forEach(this::complementRole);
        return userRoleList;
    }

    public UserRole findByUserResource(int userId, ResourceType resourceType, int resourceId) {
        UserRole userRole = userRoleMapper.findByUserResource(userId, resourceType, resourceId);
        if (userRole == null) {
            return null;
        }
        this.complementRole(userRole);
        return userRole;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_MANAGER, allEntries = true),
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_LIST_RESOURCE, key = "#resourceType.code + '_' + #resourceId")
            }
    )
    public int deleteByResource(ResourceType resourceType, Integer resourceId) {
        int result = userRoleMapper.deleteByResource(resourceType, resourceId);
        log.info("Delete user role by resource type[{}] id[{}}", resourceType.getTypeName(), resourceId);
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_MANAGER, key = "#userId"),
                    @CacheEvict(cacheNames = CACHE_NAME_U_R_LIST_RESOURCE, allEntries = true)
            }
    )
    public int deleteByUser(Integer userId) {
        int result = userRoleMapper.deleteByUser(userId);
        log.warn("Delete user[{}] all roles, entries: {}", userId, result);
        return result;
    }

    @Cacheable(cacheNames = "user_role_list_of_resource", key = "#resourceType.code + '_' + #resourceId")
    public List<UserRole> findByResource(ResourceType resourceType, Integer resourceId) {
        List<UserRole> list = userRoleMapper.findByResource(resourceType, resourceId);
        list.forEach(this::complementRole);
        return list;
    }

    private void complementRole(UserRole userRole) {
        if (userRole == null || userRole.getRoleId() == null) {
            return;
        }
        Role role = roleDao.findById(userRole.getRoleId());
        if (role == null) {
            log.error("role [{}] not exist.", userRole.getRoleId());
        } else {
            userRole.setRole(role);
        }
    }


}
