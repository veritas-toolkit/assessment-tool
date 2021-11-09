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
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.system.constant.PermissionType;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.RoleDao;
import org.veritas.assessment.system.mapper.UserRoleDao;
import org.veritas.assessment.system.mapper.UserRoleManagerDao;
import org.veritas.assessment.system.rbac.Role;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.rbac.UserRoleManager;
import org.veritas.assessment.system.service.RoleService;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class RoleServiceImpl implements RoleService {
    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private UserRoleManagerDao userRoleManagerDao;

    @Autowired
    private RoleDao roleDao;


    @Override
    @Transactional
    public boolean hasPermission(User operator, Project project, String action) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(operator.getId());
        UserRoleManager manager = userRoleManagerDao.findById(operator.getId());
        PermissionType permissionType = PermissionType.projectType(action);
        if (manager == null || permissionType == null) {
            return false;
        } else {
            return manager.hasPermission(project, permissionType);
        }
    }

    @Override
    @Transactional
    public boolean hasPermission(User operator, Group group, String action) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(operator.getId());
        UserRoleManager manager = userRoleManagerDao.findById(operator.getId());
        PermissionType permissionType = PermissionType.groupType(action);
        if (manager == null || permissionType == null) {
            return false;
        } else {
            return manager.hasPermission(group, permissionType);
        }
    }

    @Override
    @Transactional
    public Role getRole(ResourceType type, String name) {
        return roleDao.find(type.getTypeName(), name);
    }

    @Override
    public UserRole findUserRole(Integer userId, ResourceType resourceType, Integer resourceId) {
        UserRoleManager manager = userRoleManagerDao.findById(userId);
        return manager.findUserRole(resourceType, resourceId);
    }

    @Transactional
    @Override
    public UserRole grantRole(Integer userId, ResourceType resourceType, Integer resourceId,
                              RoleType roleType) {
        UserRole old = userRoleDao.findByUserResource(userId, resourceType, resourceId);
        if (old != null) {
            userRoleDao.delete(userId, resourceType, resourceId);
        }
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setCreatedTime(new Date());
        userRole.setResourceType(resourceType);
        userRole.setResourceId(resourceId);

        Role role = roleDao.find(resourceType.getTypeName(), roleType.getTypeName());
        userRole.setRoleId(role.getId());
        userRole.setRole(role);
        return userRoleDao.add(userRole);
    }

    @Override
    @Transactional
    public int removeRole(Integer userId, ResourceType resourceType, Integer resourceId) {
        return userRoleDao.delete(userId, resourceType, resourceId);
    }

    @Override
    @Transactional
    public int removeRoleByResource(ResourceType resourceType, Integer resourceId) {
        return userRoleDao.deleteByResource(resourceType, resourceId);
    }

    @Override
    public int removeRoleByUser(Integer userId) {
        return userRoleDao.deleteByUser(userId);
    }

    @Override
    @Transactional
    public List<UserRole> findByResource(ResourceType resourceType, Integer resourceId) {
        Objects.requireNonNull(resourceType);
        Objects.requireNonNull(resourceId);
        return userRoleDao.findByResource(resourceType, resourceId);
    }

    @Override
    @Transactional
    public List<UserRole> findResourceByUser(Integer userId, ResourceType resourceType) {
        return userRoleDao.findAllUserResourceType(userId, resourceType);
    }
}
