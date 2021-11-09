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
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.GroupMapper;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.RoleService;
import org.veritas.assessment.system.service.UserService;

@Component
@Slf4j
public class GroupPermissionEvaluator implements PermissionEvaluatorInterface<Group> {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectMapper projectMapper;


    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private GroupService groupService;

    @Autowired
    private RoleService roleService;


    @Override
    public boolean hasPermission(User operator, Group targetObject, String permission) {
        if (operator == null || targetObject == null) {
            return false;
        }
        if (StringUtils.isEmpty(permission)) {
            return false;
        }
        boolean result = roleService.hasPermission(operator, targetObject, permission);
        if (log.isDebugEnabled()) {
            if (result) {
                log.debug("permission: user[{}] has [{}] permission to [{}].", operator.getId(), permission, targetObject);
            } else {
                log.debug("permission: user[{}] DOES NOT have [{}] permission to [{}].", operator.getId(), permission, targetObject);
            }
        }
        return result;
    }

    @Override
    public boolean hasPermission(User operator, Integer targetId, String permission) {
        Group group = groupMapper.findById(targetId);
        if (group == null) {
            return false;
        } else {
            return hasPermission(operator, group, permission);
        }
    }

    @Override
    public String getTargetObjectType() {
        return "group";
    }
}
