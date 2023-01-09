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

package org.veritas.assessment.biz.converter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.GroupDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import java.util.Objects;

@Component
@Slf4j
public class ProjectDtoConverter implements Converter<ProjectDto, Project> {
    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupDtoConverter groupDtoConverter;

    @Override
    public ProjectDto convertFrom(Project project) {
        Objects.requireNonNull(project, "The arg[project] cannot be null.");
        ProjectDto projectDto;
        if (project.isPersonProject()) {
            User user = userService.findUserById(project.getUserOwnerId());
            UserSimpleDto userSimpleDto = new UserSimpleDto(user);
            projectDto = new ProjectDto(project, userSimpleDto);

        } else if (project.isGroupProject()) {
            Group group = groupService.findGroupById(project.getGroupOwnerId());
            GroupDto groupDto = groupDtoConverter.convertFrom(group);
            projectDto = new ProjectDto(project, groupDto);
        } else {
            throw new IllegalArgumentException("Illegal [project] object: " + project);
        }
        return projectDto;
    }
}
