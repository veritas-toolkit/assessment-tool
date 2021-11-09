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

package org.veritas.assessment.system.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.system.dto.BasicDtoInterface;
import org.veritas.assessment.system.dto.GroupCreateDto;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {

    @Autowired
    ProjectPermissionEvaluator projectPermissionEvaluator;
    @Autowired
    GroupPermissionEvaluator groupPermissionEvaluator;
    @SuppressWarnings("rawtypes")
    private Map<String, PermissionEvaluatorInterface> evaluatorInterfaceMap;
    @SuppressWarnings("rawtypes")
    private Map<Class<?>, PermissionEvaluatorInterface> classPermissionEvaluatorInterfaceMap;
    @Autowired
    private UserService userService;

    @PostConstruct
    @SuppressWarnings("rawtypes")
    public void init() {
        Map<String, PermissionEvaluatorInterface> map = new HashMap<>();
        map.put(projectPermissionEvaluator.getTargetObjectType(), projectPermissionEvaluator);
        map.put(groupPermissionEvaluator.getTargetObjectType(), groupPermissionEvaluator);
        evaluatorInterfaceMap = Collections.unmodifiableMap(map);

        Map<Class<?>, PermissionEvaluatorInterface> map2 = new HashMap<>();
        map2.put(ProjectCreateDto.class, projectPermissionEvaluator);
        map2.put(Project.class, projectPermissionEvaluator);
        map2.put(GroupCreateDto.class, groupPermissionEvaluator);
        map2.put(Group.class, groupPermissionEvaluator);
        this.classPermissionEvaluatorInterfaceMap = Collections.unmodifiableMap(map2);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public boolean hasPermission(Authentication auth, Object targetDomainObject, Object permission) {
        PermissionEvaluatorInterface permissionEvaluatorInterface =
                classPermissionEvaluatorInterfaceMap.get(targetDomainObject.getClass());
        if (permissionEvaluatorInterface == null) {
            return false;
        }
        User user = self(auth);
        if (user == null) {
            return false;
        }
        if (targetDomainObject instanceof BasicDtoInterface) {
            targetDomainObject = ((BasicDtoInterface<?>) targetDomainObject).toEntity(user.getId());
        }
        return permissionEvaluatorInterface.hasPermission(user, targetDomainObject, (String) permission);
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean hasPermission(Authentication auth, Serializable targetId, String targetType, Object permission) {
        //return Objects.equals(1, targetId);
        PermissionEvaluatorInterface permissionEvaluatorInterface = evaluatorInterfaceMap.get(targetType);
        if (permissionEvaluatorInterface == null) {
            return false;
        }
        User user = self(auth);
        if (user == null) {
            return false;
        }
        return permissionEvaluatorInterface.hasPermission(user, (Integer) targetId, (String) permission);
    }

    public User self(Authentication auth) {
//        return userService.findUserByUsernameOrEmail(auth.getName());
        return userService.findUserById(Integer.parseInt(auth.getName()));
    }
}
