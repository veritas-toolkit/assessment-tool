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

package org.veritas.assessment.biz.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.dto.BasicDtoInterface;

import java.util.Date;

@Data
public class ProjectCreateDto implements BasicDtoInterface<Project> {
    private String name;

    private String description;

    private Integer userOwnerId;

    private Integer groupOwnerId;

    private Integer businessScenario;

    private Integer questionnaireTemplateId;

    public Project toEntity(Integer creator) {
        if (StringUtils.isEmpty(name)) {
            throw new ErrorParamException("'name' cannot be empty.");
        }
        if (businessScenario == null) {
            throw new ErrorParamException("'businessScenario' cannot be empty.");
        }
        boolean userOwner = userOwnerId != null && groupOwnerId == null;
        boolean groupOwner = userOwnerId == null && groupOwnerId != null;
        if (!userOwner && !groupOwner) {
            throw new ErrorParamException("One and only one of 'userOwnerId' and 'groupOwnerId' is not empty.");
        }
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setBusinessScenario(businessScenario);
        project.setCreatorUserId(creator);
        project.setUserOwnerId(userOwnerId);
        project.setGroupOwnerId(groupOwnerId);
        Date now = new Date();
        project.setCreatedTime(now);
        project.setLastEditedTime(now);
        return project;
    }

}
