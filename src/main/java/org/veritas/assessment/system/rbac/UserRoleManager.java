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

package org.veritas.assessment.system.rbac;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.system.constant.PermissionType;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.entity.Group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Slf4j
public class UserRoleManager {
    @Getter
    private final List<UserRole> userRoleList;
    @Getter
    private Map<Integer, UserRole> projectUserRoleMap;
    @Getter
    private Map<Integer, UserRole> groupUserRoleMap;
    @Getter
    private List<UserRole> otherUserRoleList;
    private final MultiValuedMap<ResourceType, UserRole> userRoleMap;


    public UserRoleManager(List<UserRole> userRoleList) {
        if (userRoleList == null) {
            this.userRoleList = Collections.emptyList();
        } else {
            this.userRoleList = Collections.unmodifiableList(userRoleList);
        }
        this.init();
        userRoleMap = new ArrayListValuedHashMap<>();
        this.userRoleList.forEach(userRole -> userRoleMap.put(userRole.getResourceType(), userRole));
    }

    public UserRole findUserRole(ResourceType resourceType, Integer resourceId) {
        Objects.requireNonNull(resourceType);
        Objects.requireNonNull(resourceId);
        switch (resourceType) {
            case PROJECT:
                return projectUserRoleMap.get(resourceId);
            case GROUP:
                return groupUserRoleMap.get(resourceId);
            default:
                throw new IllegalStateException("Should not run here.");
        }
    }

    private void init() {
        Map<Integer, UserRole> projectUserRoleMap = new LinkedHashMap<>();
        Map<Integer, UserRole> groupUserRoleMap = new LinkedHashMap<>();
        List<UserRole> otherUserRoleList = new ArrayList<>();
        for (UserRole userRole : this.userRoleList) {
            if (!userRole.enable()) {
                continue;
            }
            if (userRole.ofProject()) {
                projectUserRoleMap.put(userRole.getResourceId(), userRole);
            } else if (userRole.ofGroup()) {
                groupUserRoleMap.put(userRole.getResourceId(), userRole);
            } else {
                otherUserRoleList.add(userRole);
            }
        }
        this.projectUserRoleMap = Collections.unmodifiableMap(projectUserRoleMap);
        this.groupUserRoleMap = Collections.unmodifiableMap(groupUserRoleMap);
        this.otherUserRoleList = Collections.unmodifiableList(otherUserRoleList);
    }

    public boolean hasPermission(Project project, PermissionType permissionType) {
        if (permissionType == PermissionType.PROJECT_CREATE) {
            if (project.isPersonProject()) {
                return true;
            }
        }
        Set<PermissionType> projectEditSet = new HashSet<>();
        projectEditSet.add(PermissionType.PROJECT_EDIT);
        projectEditSet.add(PermissionType.PROJECT_EDIT_QUESTIONNAIRE);
        projectEditSet.add(PermissionType.PROJECT_UPLOAD_JSON);
        projectEditSet.add(PermissionType.PROJECT_INPUT_ANSWER);
        if (project.isArchived() && projectEditSet.contains(permissionType)) {
            log.warn("The project has been archived, cannot be edited.");
            throw new IllegalRequestException("The project has been archived. Cannot be edited.");
        }
        for (UserRole userRole : userRoleList) {
            if (userRole.hasPermission(project, permissionType)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(Group group, PermissionType permissionType) {

        for (UserRole userRole : userRoleList) {
            if (userRole.hasPermission(group, permissionType)) {
                return true;
            }
        }
        return false;
    }
}
