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

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.system.dto.BasicDtoInterface;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Objects;

@Data
public class ProjectCreateDto implements BasicDtoInterface<Project> {

    @Schema(description = "Name of the project.", required = true)
    @NotEmpty(message = "Project's name cannot be empty.")
    private String name;

    @Schema(description = "Description of the project.", required = true)
    @NotEmpty(message = "Project's description cannot be empty.")
    private String description;

    @Schema(description = "The owner's user id, if the project belongs to a user.")
    private Integer userOwnerId;

    @Schema(description = "The owner's group id, if the project belongs to a group.")
    private Integer groupOwnerId;

    @Schema(description = "Business scenario of the project.", required = true)
    @NotNull(message = "Please choose a business scenario.")
    private Integer businessScenario;

    @Schema(description = "Created questionnaire from questionnaire template.")
    private Integer questionnaireTemplateId;

    @Schema(description = "Copy questionnaire from the other project.")
    private Integer copyFromProjectId;

    @Schema(description = "Assess generic principle.", required = true, defaultValue = "true")
    private Boolean principleGeneric = true;

    @Schema(description = "Assess fairness principle.", required = true)
    private Boolean principleFairness;

    @Schema(description = "Assess ethic and accountable principle.", required = true)
    private Boolean principleEA;

    @Schema(description = "Assess transparency principle.", required = true)
    private Boolean principleTransparency;

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
        if (Objects.isNull(this.getCopyFromProjectId()) && Objects.isNull(this.getQuestionnaireTemplateId())) {
            throw new IllegalRequestException("Choose a questionnaire template or project to create questionnaire.");
        }
        Project project = new Project();
        project.setName(name);
        project.setDescription(description);
        project.setBusinessScenario(businessScenario);
        project.setCreatorUserId(creator);
        project.setUserOwnerId(userOwnerId);
        project.setGroupOwnerId(groupOwnerId);
        project.setPrincipleGeneric(this.principleGeneric);
        project.setPrincipleFairness(this.principleFairness);
        project.setPrincipleEA(this.principleEA);
        project.setPrincipleTransparency(this.principleTransparency);
        project.setArchived(false);
        Date now = new Date();
        project.setCreatedTime(now);
        project.setLastEditedTime(now);
        return project;
    }

}
