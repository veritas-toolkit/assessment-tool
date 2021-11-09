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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.RoleService;
import org.veritas.assessment.system.service.UserService;

@Component
@Slf4j
public class ProjectPermissionEvaluator implements PermissionEvaluatorInterface<Project> {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectMapper projectMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public boolean hasPermission(User operator, Project targetObject, String permission) {
        if (operator == null || targetObject == null) {
            return false;
        }
        if (StringUtils.isEmpty(permission)) {
            return false;
        }
        boolean result = roleService.hasPermission(operator, targetObject, permission);
        if (result) {
            if (log.isDebugEnabled()) {
                log.debug("permission: user[{}] has [{}] permission to [{}].", operator.getId(), permission, targetObject);
            }
        } else {
            log.warn("permission: user[{}] DOES NOT have [{}] permission to [{}].", operator.getId(), permission, targetObject);
        }

        return result;
    }

    @Override
    public boolean hasPermission(User operator, Integer targetId, String permission) {
        Project project = projectMapper.findById(targetId);
        if (project == null) {
            return false;
        } else {
            return hasPermission(operator, project, permission);
        }
    }

    @Override
    public String getTargetObjectType() {
        return "project";
    }
}
