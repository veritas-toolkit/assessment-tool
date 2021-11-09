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
import org.springframework.stereotype.Repository;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.rbac.UserRole;

import java.util.List;
import java.util.Objects;

@Repository
interface UserRoleMapper extends BaseMapper<UserRole> {

    default List<UserRole> findByUserId(int userId) {
        return this.findByUserIdResourceType(userId, null);
    }

    default List<UserRole> findByUserIdResourceType(int userId, ResourceType resourceType) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        if (resourceType != null) {
            wrapper.eq(UserRole::getResourceType, resourceType);
        }
        wrapper.orderByDesc(UserRole::getCreatedTime);
        return selectList(wrapper);
    }

    default UserRole findByUserProject(int userId, int projectId) {
        return findByUserResource(userId, ResourceType.PROJECT, projectId);
    }

    default UserRole findByUserGroup(int userId, int groupId) {
        return findByUserResource(userId, ResourceType.GROUP, groupId);
    }

    default UserRole findByUserResource(int userId, ResourceType resourceType, int resourceId) {
        Objects.requireNonNull(resourceType);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        wrapper.eq(UserRole::getResourceType, resourceType);
        wrapper.eq(UserRole::getResourceId, resourceId);
        return selectOne(wrapper);
    }

    default List<UserRole> findByResource(ResourceType resourceType, Integer resourceId) {
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getResourceType, resourceType);
        wrapper.eq(UserRole::getResourceId, resourceId);
        return selectList(wrapper);
    }


    default int deleteByResource(ResourceType resourceType, Integer resourceId) {
        Objects.requireNonNull(resourceType);
        Objects.requireNonNull(resourceId);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getResourceType, resourceType);
        wrapper.eq(UserRole::getResourceId, resourceId);
        return delete(wrapper);
    }

    default int deleteByUser(Integer userId) {
        Objects.requireNonNull(userId);
        LambdaQueryWrapper<UserRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRole::getUserId, userId);
        return delete(wrapper);
    }
}
