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
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.entity.Project;

import java.util.Date;

@Data
@NoArgsConstructor
public class ProjectDto {
    private Integer id;

    private String name;

    private String description;

    private Integer userOwnerId;

    private Integer groupOwnerId;

    private Integer businessScenario;

    private Boolean principleGeneric;

    private Boolean principleFairness;

    private Boolean principleEA;

    private Boolean principleTransparency;

    private Integer currentModelArtifactVid;

    private Integer currentQuestionnaireVid;

    private Integer creatorUserId;

    private Date createdTime;

    private Date lastEditedTime;

    private boolean archived;

    private ProjectAssessmentProgressDto assessmentProgres;

    private UserSimpleDto userOwner;

    private GroupDto groupOwner;

    public ProjectDto(Project project, UserSimpleDto userOwner) {
        BeanUtils.copyProperties(project, this);
        this.userOwner = userOwner;
    }

    public ProjectDto(Project project, GroupDto groupOwner) {
        BeanUtils.copyProperties(project, this);
        this.groupOwner = groupOwner;
    }
}
