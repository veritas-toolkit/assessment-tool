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
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.system.dto.BasicDtoInterface;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class ProjectBasicDto implements BasicDtoInterface<Project> {
    private Integer id;

    @NotBlank
    private String name;

    @NotBlank
    private String description;

    private Integer businessScenario;


    public Project toEntity(Integer creator) {
        Project project = new Project();
        project.setId(id);
        project.setName(name);
        project.setDescription(description);
        project.setBusinessScenario(this.businessScenario);
        Date now = new Date();
        project.setLastEditedTime(now);
        return project;
    }
}
