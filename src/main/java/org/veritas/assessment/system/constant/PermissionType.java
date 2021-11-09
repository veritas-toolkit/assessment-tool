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

package org.veritas.assessment.system.constant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import static org.veritas.assessment.system.constant.ResourceType.GROUP;
import static org.veritas.assessment.system.constant.ResourceType.PROJECT;

@Getter
@Slf4j
public enum PermissionType {
    PROJECT_CREATE(PROJECT, "project", "create"),
    PROJECT_DELETE(PROJECT, "project", "delete"),
    PROJECT_EDIT(PROJECT, "project", "edit"),
    PROJECT_READ(PROJECT, "project", "read"),
    PROJECT_MANAGE_MEMBERS(PROJECT, "project", "manage members"),
    PROJECT_EDIT_QUESTIONNAIRE(PROJECT, "project", "edit questionnaire"),
    PROJECT_UPLOAD_JSON(PROJECT, "project", "upload json"),
    PROJECT_INPUT_ANSWER(PROJECT, "project", "input answer"),
    GROUP_CREATE(GROUP, "group", "create"),
    GROUP_DELETE(GROUP, "group", "delete"),
    GROUP_EDIT(GROUP, "group", "edit"),
    GROUP_READ(GROUP, "group", "read"),
    GROUP_MANAGE_MEMBERS(GROUP, "group", "manage members"),
    ;

    private static final String TARGET_PROJECT = "project";
    private static final String TARGET_GROUP = "group";
    private final ResourceType resourceType;
    private final String target;
    private final String action;

    PermissionType(ResourceType resourceType, String target, String action) {
        this.resourceType = resourceType;
        this.target = target;
        this.action = action;
    }

    public static PermissionType projectType(String action) {
        for (PermissionType type : PermissionType.values()) {
            if (!StringUtils.equals(TARGET_PROJECT, type.getTarget())) {
                continue;
            }
            if (StringUtils.equals(action, type.getAction())) {
                return type;
            }
        }
        log.warn("There is not action[{}]", action);
        return null;
    }

    public static PermissionType groupType(String action) {
        for (PermissionType type : PermissionType.values()) {
            if (!StringUtils.equals(TARGET_GROUP, type.getTarget())) {
                continue;
            }
            if (StringUtils.equals(action, type.getAction())) {
                return type;
            }
        }
        return null;
    }
}