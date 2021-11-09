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

package org.veritas.assessment.system.service;

import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.rbac.Role;
import org.veritas.assessment.system.rbac.UserRole;

import java.util.List;

public interface RoleService {

    boolean hasPermission(User operator, Project project, String action);

    boolean hasPermission(User operator, Group group, String action);

    Role getRole(ResourceType type, String name);

    UserRole findUserRole(Integer userId, ResourceType resourceType, Integer resourceId);

    UserRole grantRole(Integer userId, ResourceType resourceType, Integer resourceId, RoleType roleType);

    int removeRole(Integer userId, ResourceType resourceType, Integer resourceId);

    int removeRoleByResource(ResourceType resourceType, Integer resourceId);

    int removeRoleByUser(Integer userId);

    List<UserRole> findByResource(ResourceType resourceType, Integer resourceId);


    List<UserRole> findResourceByUser(Integer userId, ResourceType resourceType);

}
