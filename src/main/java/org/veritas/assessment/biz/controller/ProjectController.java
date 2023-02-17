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

package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.veritas.assessment.biz.converter.ProjectDtoConverter;
import org.veritas.assessment.biz.dto.ModelArtifactDto;
import org.veritas.assessment.biz.dto.ProjectBasicDto;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.dto.ProjectDetailDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.RoleDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.PrincipleAssessmentProgressDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.ImageService;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.FileSystemException;
import org.veritas.assessment.common.exception.IllegalDataException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.RoleService;
import org.veritas.assessment.system.service.UserService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/project")
public class ProjectController {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;
    @Autowired
    ImageService imageService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectDtoConverter projectDtoConverter;
    @Autowired
    private ModelArtifactService modelArtifactService;
    @Autowired
    private ProjectReportService reportService;
    @Autowired
    private RoleService roleService;

    @Autowired
    TemplateQuestionnaireService templateQuestionnaireService;

    @Operation(summary = "Get pageable project list which current user can access.")
    @GetMapping("")
    public Pageable<ProjectDto> listProject(
            @Parameter(hidden = true) User operator,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        Pageable<Project> pageable = projectService.findProjectPageable(operator, prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Get all projects which created by the operator.")
    @GetMapping("/my-projects")
    public Pageable<ProjectDto> listProjectCreatedByOperator(
            @Parameter(hidden = true) User operator,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize) {
        Pageable<Project> pageable = projectService.findProjectPageableByCreator(operator, prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }


    @Operation(summary = "Create a project")
    @PostMapping("/new")
    @ResponseStatus(code = HttpStatus.CREATED)
    @PreAuthorize("hasPermission(#dto, 'create')")
    public ProjectDto createProject(@Parameter(hidden = true) User operator,
                                    @RequestBody ProjectCreateDto dto) {
        if (dto.getQuestionnaireTemplateId() == null && dto.getCopyFromProjectId() == null) {
            throw new ErrorParamException("Creating project needs questionnaire template id or copy from other project.");
        }
        Project project = dto.toEntity(operator.getId());
        Integer templateId = dto.getQuestionnaireTemplateId();
        Integer copyFromProjectId = dto.getCopyFromProjectId();
        Project newProject = null;

        if (templateId != null) {
            TemplateQuestionnaire template = templateQuestionnaireService.findByTemplateId(templateId);
            if (template == null) {

            }
            newProject = projectService.createProject(operator, project, template);
        } else if (copyFromProjectId != null) {
            Project old = projectService.findProjectById(copyFromProjectId);
            if (old == null) {

            }
            newProject = projectService.createProject(operator, project, old);
        } else {
            throw new IllegalDataException("Choose a questionnaire template or copy from other project's questionnaire.");
        }
        return projectDtoConverter.convertFrom(newProject);
    }

    @Operation(summary = "Get the project information.")
    @GetMapping("/{projectId}")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public ProjectDto getProject(@Parameter(hidden = true) User operator,
                                 @PathVariable("projectId") int projectId) {
        Project project = projectService.findProjectById(projectId);
        return projectDtoConverter.convertFrom(project);
    }

    private QuestionnaireService questionnaireService;

    @Operation(summary = "Get the project information.")
    @GetMapping("/{projectId}/detail")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public ProjectDetailDto getProjectDetail(@Parameter(hidden = true) User operator,
                                             @PathVariable("projectId") int projectId) {
        Project project = projectService.findProjectById(projectId);
        ProjectDto projectDto = projectDtoConverter.convertFrom(project);


        ProjectDetailDto projectDetailDto = new ProjectDetailDto();
        projectDetailDto.setProject(projectDto);
        // FIXME: 2023/1/31 query from the database.
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectId);
        projectDetailDto.setProgressList(PrincipleAssessmentProgressDto.from(project, questionnaireVersion));


        UserRole userRole = roleService.findUserRole(operator.getId(), ResourceType.PROJECT, projectId);
        if (userRole != null) {
            RoleDto roleDto = new RoleDto(userRole.getRole());
            projectDetailDto.setProjectRole(roleDto);
        }
        if (project.isGroupProject()) {
            UserRole userRoleOfGroup = roleService.findUserRole(operator.getId(),
                    ResourceType.GROUP, project.getGroupOwnerId());
            if (userRoleOfGroup != null) {
                RoleDto roleDto = new RoleDto(userRoleOfGroup.getRole());
                projectDetailDto.setGroupRole(roleDto);
            }
        }
        return projectDetailDto;
    }

    @Operation(summary = "Delete the project.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'delete')")
    @DeleteMapping("/{projectId}")
    public void deleteProject(@PathVariable("projectId") int projectId) {
        projectService.delete(projectId);
    }

    @Operation(summary = "Modify the basic information of the project.")
    @PostMapping("/{projectId}")
    @PreAuthorize("hasPermission(#projectId, 'project', 'edit')")
    public ProjectDto modifyProject(@PathVariable("projectId") int projectId,
                                    @RequestBody ProjectBasicDto dto) {
        Project project = dto.toEntity();
        if (project.getId() == null) {
            project.setId(projectId);
        } else if (project.getId() != projectId) {
            throw new ErrorParamException("You cannot modify project:" + project.getId());
        }
        Project newProject = projectService.modifyProject(project);
        return projectDtoConverter.convertFrom(newProject);
    }

    @Operation(summary = "Get all the members of the project.")
    @GetMapping("/{projectId}/member")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<Member> listMember(@PathVariable("projectId") Integer projectId) {
        return projectService.findMemberList(projectId);

    }

    @Operation(summary = "Add members of the project.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'manage members')")
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
    @PreAuthorize("hasPermission(#projectId, 'project', 'manage members')")
    public void deleteMember(@PathVariable("projectId") Integer projectId,
                             @PathVariable("userId") Integer userId) {
        projectService.removeMember(projectId, userId);
    }

    @Operation(summary = "Change a member's role of the project.")
    @PostMapping("/{projectId}/member")
    @PreAuthorize("hasPermission(#projectId, 'project', 'manage members')")
    public Member modifyMember(@PathVariable("projectId") Integer projectId,
                               @RequestBody MembershipDto membershipDto) {
        if (membershipDto.getUserId() == null || membershipDto.getType() == null) {
            throw new ErrorParamException("Params [userId, type] can't be null");
        }
        return projectService.modifyMember(projectId, membershipDto.getUserId(), membershipDto.getType());
    }

    @Operation(summary = "Upload the model artifact file(json).")
    @PreAuthorize("hasPermission(#projectId, 'project', 'upload json')")
    @PostMapping("/{projectId}/modelArtifact")
    public ModelArtifactDto uploadJson(@Parameter(hidden = true) User operator,
                                       @PathVariable("projectId") Integer projectId,
                                       @RequestParam("file") MultipartFile file) {
        String json;
        try (InputStream inputStream = file.getInputStream()) {
            json = IOUtils.toString(inputStream, Charset.defaultCharset());
        } catch (IOException ioException) {
            log.warn("Read json file failed.", ioException);
            throw new ErrorParamException("Upload file failed.");
        }
        log.debug("json content sha256: {}", DigestUtils.sha256Hex(json));
        String filename = file.getOriginalFilename();
        filename = FilenameUtils.getName(filename);
        ModelArtifact modelArtifact = modelArtifactService.create(projectId, json, filename);
        // save file out of transaction.
        modelArtifactService.saveJsonFile(modelArtifact);

        modelArtifact.setUploadUserId(operator.getId());
        modelArtifact.setUploadTime(new Date());

        projectService.updateModelArtifact(operator, projectId, modelArtifact);
        return new ModelArtifactDto(modelArtifact);
    }

    @Operation(summary = "Get the basic information of the model artifact file(json).")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/{projectId}/modelArtifact")
    public ModelArtifactDto getJsonInfo(@PathVariable("projectId") Integer projectId) {
        ModelArtifact modelArtifact = modelArtifactService.findCurrent(projectId);
        if (modelArtifact == null) {
            throw new NotFoundException("There is no model artifact for this project.");
        }
        return new ModelArtifactDto(modelArtifact);
    }

    @Operation(summary = "Download the model artifact file(json).")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/{projectId}/modelArtifact/download")
    public HttpEntity<String> downloadJson(@PathVariable("projectId") Integer projectId) {
        ModelArtifact modelArtifact = modelArtifactService.findCurrent(projectId);
        if (modelArtifact == null) {
            throw new NotFoundException(String.format("project[%d] haven't uploaded any model artifacts.", projectId));
        }
        try {
            modelArtifactService.loadContent(modelArtifact);
        } catch (IOException exception) {
            throw new NotFoundException("File not found or deleted.");
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + modelArtifact.getFilename().replace(" ", "_"));
        header.setContentLength(modelArtifact.getJsonContent().getBytes(StandardCharsets.UTF_8).length);
        return new HttpEntity<>(modelArtifact.getJsonContent(), header);
    }

    @Operation(summary = "Upload the image of the project's report.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @RequestMapping(path = "/{projectId}/image", method = {RequestMethod.PUT, RequestMethod.POST})
    public HttpEntity<String> uploadImage(@PathVariable("projectId") Integer projectId,
                                          @RequestParam("image") MultipartFile image) {
        byte[] content = null;
        try (InputStream inputStream = image.getInputStream()) {
            content = IOUtils.toByteArray(inputStream);
        } catch (IOException ioException) {
            log.warn("Read image file failed.", ioException);
            throw new ErrorParamException("Upload image failed.");
        }
        String imageFileName = image.getOriginalFilename();
        String filename = imageService.saveImage(projectId, imageFileName, content);
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE);
        return new HttpEntity<>(filename, header);
    }

    @Operation(summary = "Get the images of the project's report.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/{projectId}/image/{image}")
    public HttpEntity<byte[]> getImage(@PathVariable("projectId") Integer projectId,
                                       @PathVariable("image") String image) {
        HttpHeaders header = new HttpHeaders();
        header.set(HttpHeaders.EXPIRES, "0");
        header.setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        Optional<MediaType> optionalMediaType = MediaTypeFactory.getMediaType(image);
        optionalMediaType.ifPresent(mediaType -> header.set(HttpHeaders.CONTENT_TYPE, mediaType.toString()));
        byte[] content;
        try {
            content = imageService.getImage(projectId, image);
        } catch (FileNotFoundException fileNotFoundException) {
            throw new NotFoundException("Not found the image", fileNotFoundException);
        } catch (IOException exception) {
            throw new FileSystemException("Read the image failed.", exception);
        }
        return new HttpEntity<>(content, header);
    }

}
