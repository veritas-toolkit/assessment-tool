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

package org.veritas.assessment.biz.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.ProjectDtoConverter;
import org.veritas.assessment.biz.dto.ProjectBasicDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectQueryService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/admin/project")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminProjectController {

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectQueryService projectQueryService;

    @Autowired
    private ProjectDtoConverter projectDtoConverter;

    @Operation(summary = "Admin: list projects pageable.")
    @GetMapping("")
    public Pageable<ProjectDto> listProject(@RequestParam(name = "prefix", required = false) String prefix,
                                            @RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                                            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable<Project> pageable = projectQueryService.findProjectPageable(prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Get the project information.")
    @GetMapping("/{projectId}")
    public ProjectDto getProject(@Parameter(hidden = true) User operator,
                                 @PathVariable("projectId") int projectId) {
        Project project = projectService.findProjectById(projectId);
        return projectDtoConverter.convertFrom(project);
    }

    @Operation(summary = "Admin: delete the project.")
    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable("projectId") int projectId) {
        projectService.delete(projectId);
    }

    // edit project
    @Operation(summary = "Admin: modify the basic information of the project.")
    @PostMapping("/{projectId}")
    public ProjectDto modifyProject(@Parameter(hidden = true) User operator,
                                    @PathVariable("projectId") int projectId,
                                    @Valid @RequestBody ProjectBasicDto dto) {
        if (dto.getId() == null) {
            dto.setId(projectId);
        } else if (dto.getId() != projectId) {
            throw new ErrorParamException("You cannot modify project:" + dto.getId());
        }
        Project newProject = projectService.modifyProject(dto.toEntity());
        return projectDtoConverter.convertFrom(newProject);
    }


    @Operation(summary = "Admin: list all the members of the project.")
    @GetMapping("/{projectId}/member")
    public List<Member> listMember(@PathVariable("projectId") Integer projectId) {
        return projectService.findMemberList(projectId);

    }

    @Operation(summary = "Add members of the project.")
    @PutMapping("/{projectId}/member")
    public List<Member> addMemberList(@PathVariable("projectId") Integer projectId,
                                      @RequestBody List<MembershipDto> membershipDtoList) {
        Objects.requireNonNull(projectId);
        Objects.requireNonNull(membershipDtoList);
        membershipDtoList.forEach(membershipDto -> {
            if (membershipDto.getUserId() == null || membershipDto.getType() == null) {
                throw new ErrorParamException("[userId] and [type] can not be null.");
            }
        });
        return projectService.addMemberList(projectId, membershipDtoList);
    }


    @Operation(summary = "Delete a member from the project.")
    @DeleteMapping("/{projectId}/member/{userId}")
    public void deleteMember(@PathVariable("projectId") Integer projectId,
                             @PathVariable("userId") Integer userId) {
        projectService.removeMember(projectId, userId);
    }

    @Operation(summary = "Change a member's role of the project.")
    @PostMapping("/{projectId}/member")
    public Member modifyMember(@PathVariable("projectId") Integer projectId,
                               @RequestBody MembershipDto membershipDto) {
        if (membershipDto.getUserId() == null || membershipDto.getType() == null) {
            throw new ErrorParamException("Params [userId, type] can't be null");
        }
        return projectService.modifyMember(projectId, membershipDto.getUserId(), membershipDto.getType());
    }

}
