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

package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.action.EditAnswerAction;
import org.veritas.assessment.biz.action.QueryProjectPageableAction;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.util.PersistenceExceptionUtils;
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.IllegalDataException;
import org.veritas.assessment.common.exception.PermissionException;
import org.veritas.assessment.common.exception.QuotaException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.PermissionType;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.RoleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private GroupService groupService;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ModelInsightService modelInsightService;
    @Autowired
    private ModelArtifactService modelArtifactService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Override
    @Transactional
    public Project createProject(User operator, Project project, TemplateQuestionnaire template) {
        Objects.requireNonNull(template);
        return innerCreateProject(operator, project, template, null);
    }

    @Override
    @Transactional
    public Project createProject(User operator, Project project, Project copyFromProject) {
        Objects.requireNonNull(copyFromProject);
        return innerCreateProject(operator, project, null, copyFromProject);
    }

    /**
     * Create a new project.
     * <br/>
     * <pre>
     * Steps:
     *  1. Create basic project.
     *  2. Grant role and permission for the creator.
     *  3. Copy questionnaire from questionnaire-template or other project's.
     * </pre>
     * @param creator The project's creator. The owner of the project.
     * @param project
     * @return
     */
    private Project innerCreateProject(User creator, Project project,
                                       TemplateQuestionnaire template, Project copyFromProject) {

        if (template == null && copyFromProject == null) {
            throw new IllegalDataException("");
        }
        int operatorId = creator.getId();
        long created = projectMapper.numberOfProjectCreatedByUser(operatorId);
        if (created >= creator.getProjectLimited()) {
            log.warn("User [{}] has created too much projects[{}].", creator.identification(), created);
            throw new QuotaException("Your quota of project creating is " +
                    creator.getProjectLimited() +
                    ", and can't create more project.");
        }
        if (project.isPersonProject()) {
            if (!Objects.equals(project.getOwnerId(), operatorId)) {
                log.warn("User[{}] want to create a project for user[{}]. rejected!",
                        creator.identification(), project.getOwnerId());
                throw new ErrorParamException("You cannot create project for other users.");
            }
        } else if (project.isGroupProject()) {
            Integer groupId = project.getOwnerId();
            Group group = groupService.findGroupById(groupId);
            if (group == null) {
                log.warn("The group id[{}] is not exist.", groupId);
                throw new ErrorParamException(String.format("The group[%d] does not exist.", groupId));
            }
            boolean permission = roleService.hasPermission(creator, group, PermissionType.PROJECT_CREATE.getAction());
            if (!permission) {
                log.warn("The user[{}] does not has permission to create a project for the group[{}]",
                        creator.identification(), group.identification());
                throw new ErrorParamException(
                        String.format("You do not have permission create a project for the group[%s]",
                                group.getName()));
            }
        } else {
            log.warn("The request doest appoint the owner of the project.");
            throw new ErrorParamException("You should appoint an owner(yourself or a group) for the project.");
        }

        project.setCreatorUserId(operatorId);
        Date createdTime = new Date();
        project.setCreatedTime( createdTime);
        project.setLastEditedTime( createdTime);


        try {
            projectMapper.addProject(project);
            QuestionnaireVersion questionnaire = null;
            if (template != null) {
                questionnaire = questionnaireService.createByTemplate(operatorId, project, createdTime, template);;
            } else {
                questionnaire = questionnaireService.copyFrom(operatorId, project, createdTime, copyFromProject);
            }
            project.setCurrentQuestionnaireVid(questionnaire.getVid());
            projectMapper.updateQuestionnaireVid(project);

            questionnaireService.saveNewQuestionnaire(questionnaire);
            roleService.grantRole(operatorId, ResourceType.PROJECT, project.getId(), RoleType.OWNER);
        } catch (PersistenceException exception) {
            exceptionHandler(exception, project);
        }
        return project;
    }

    @Override
    @Transactional
    public int delete(Integer projectId) {
        Project project = projectMapper.findById(projectId);
        int result = projectMapper.delete(project);
        if (result <= 0) {
            log.warn("Delete project[{}] failed.", projectId);
        }
        roleService.removeRoleByResource(ResourceType.PROJECT, projectId);
        return result;
    }

    @Override
    @Transactional
    public int archive(Integer projectId) {
        return projectMapper.archive(projectId);
    }

    @Override
    @Transactional
    public int unarchive(Integer projectId) {
        return projectMapper.unarchive(projectId);
    }

    @Override
    @Transactional
    public int deleteProjectByUserOwner(Integer userOwnerId) {
        Objects.requireNonNull(userOwnerId);
        List<Project> projectList = projectMapper.findProjectByUserOwner(userOwnerId);
        int result = 0;
        for (Project project : projectList) {
            result += this.delete(project.getId());
        }
        return result;
    }

    @Override
    @Transactional
    public int deleteProjectByGroupOwner(Integer ownerId) {
        Objects.requireNonNull(ownerId);
        List<Project> projectList = projectMapper.findProjectByGroupOwner(ownerId);
        int result = 0;
        for (Project project : projectList) {
            result += this.delete(project.getId());
        }
        return result;
    }

    @Override
    @Transactional
    public Project findProjectById(int projectId) throws PermissionException {
        return this.projectMapper.findById(projectId);
    }

    @Override
    @Transactional
    public Project modifyProject(Project project) {
        Project old = projectMapper.findById(project.getId());
        project.setLastEditedTime(new Date());
        try {
            projectMapper.updateBasicInfo(old, project);
        } catch (PersistenceException exception) {
            exceptionHandler(exception, project);
        }
        return this.projectMapper.findById(project.getId());
    }

    @Override
    @Transactional
    public Member addMember(Integer projectId, Integer userId, RoleType roleType) {
        User user = userMapper.findById(userId);
        Project project = projectMapper.findById(projectId);
        if (project.isPersonProject()) {
            if (Objects.equals(project.getOwnerId(), userId)) {
                throw new ErrorParamException("Cannot modify the user who is the creator of individual project.");
            }
        }
        return addMemberInternal(user, projectId, roleType);
    }

    private Member addMemberInternal(User user, Integer projectId, RoleType roleType) {
        UserRole userRole = roleService.grantRole(user.getId(), ResourceType.PROJECT, projectId, roleType);
        return new Member(user, userRole);
    }

    @Override
    @Transactional
    public int removeMember(int projectId, int userId) {
        UserRole userRole = roleService.findUserRole(userId, ResourceType.PROJECT, projectId);
        if (userRole == null) {
            log.warn("Project[{}] does not has member, id: {}", projectId, userId);
            return 0;
        } else {
            Project project = projectMapper.findById(projectId);
            if (project.isPersonProject()) {
                if (project.getOwnerId() == userId) {
                    throw new ErrorParamException("Cannot delete the user who is the creator of individual project.");
                }
            }
        }
        return roleService.removeRole(userId, ResourceType.PROJECT, projectId);
    }

    @Override
    @Transactional
    public Member modifyMember(Integer projectId, Integer userId, RoleType roleType) {
        User user = userMapper.findById(userId);
        Project project = projectMapper.findById(projectId);
        if (project.isPersonProject()) {
            if (Objects.equals(project.getOwnerId(), userId)) {
                throw new ErrorParamException("Cannot modify the user who is the creator of individual project.");
            }
        }

        roleService.removeRole(userId, ResourceType.PROJECT, projectId);
        return addMemberInternal(user, projectId, roleType);
    }

    /**
     * Find all member of project, including project members and project's group members.
     * <br/>
     * If the project is an individual project, return all the project's members. If the project is a group's
     * project, return project's members and group's members. And if one user belongs to both project and group,
     * only return as project member.
     *
     * @param projectId id of the project.
     * @return all the members.
     */
    @Override
    @Transactional
    public List<Member> findMemberList(int projectId) {
        Project project = projectMapper.findById(projectId);
        List<UserRole> projectUserRoleList = roleService.findByResource(ResourceType.PROJECT, projectId);
        List<UserRole> userRoleList;
        if (project.isGroupProject()) {
            List<UserRole> groupUserRoleList = roleService.findByResource(ResourceType.GROUP,
                    project.getGroupOwnerId());
            userRoleList = new ArrayList<>(projectUserRoleList.size() + groupUserRoleList.size());
            userRoleList.addAll(projectUserRoleList);
            Set<Integer> userIdSet = new HashSet<>();
            projectUserRoleList.forEach(userRole -> userIdSet.add(userRole.getUserId()));
            groupUserRoleList.forEach(userRole -> {
                if (!userIdSet.contains(userRole.getUserId())) {
                    userRoleList.add(userRole);
                }
            });
        } else {
            userRoleList = new ArrayList<>(projectUserRoleList);
        }
        List<Member> memberList = new ArrayList<>(userRoleList.size());
        userRoleList.forEach(userRole -> {
            User user = userMapper.findById(userRole.getUserId());
            if (user != null) {
                Member member = new Member(user, userRole);
                memberList.add(member);
            } else {
                log.warn("user[{}] is not found.", userRole.getUserId());
            }
        });
        return memberList;
    }

    @Override
    @Transactional(readOnly = true)
    public Pageable<Project> findProjectPageable(User operator, QueryProjectPageableAction queryAction) {
        List<Integer> projectIdList = findResourceIdList(operator.getId(), ResourceType.PROJECT);
        List<Integer> groupIdList = findResourceIdList(operator.getId(), ResourceType.GROUP);
        return projectMapper.findProjectPageable(projectIdList, groupIdList, queryAction);
    }

    @Override
    public Pageable<Project> findProjectPageable(Integer userId, String prefix, String keyword, int page, int pageSize) {
        List<Integer> projectIdList = findResourceIdList(userId, ResourceType.PROJECT);
        List<Integer> groupIdList = findResourceIdList(userId, ResourceType.GROUP);
        return projectMapper.findProjectPageable(projectIdList, groupIdList, prefix, keyword, page, pageSize);
    }

    @Override
    public List<Project> findProjectList(Integer userId) {
        List<Integer> projectIdList = findResourceIdList(userId, ResourceType.PROJECT);
        List<Integer> groupIdList = findResourceIdList(userId, ResourceType.GROUP);
        return projectMapper.findProjectList(projectIdList, groupIdList);
    }

    @Override
    @Transactional
    public Pageable<Project> findProjectPageableByCreator(User user, String prefix, String keyword, int page, int pageSize) {
        return projectMapper.findProjectPageableByCreator(user.getId(), prefix, keyword, page, pageSize);

    }

    private List<Integer> findResourceIdList(Integer userId, ResourceType resourceType) {
        List<UserRole> list = roleService.findResourceByUser(userId, resourceType);
        return list.stream().map(UserRole::getResourceId).collect(Collectors.toList());
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Project> findByUserOwnerId(Integer userId) {
        return projectMapper.findProjectByUserOwner(userId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Project> findByGroupOwnerId(Integer groupId) {
        Objects.requireNonNull(groupId);
        return projectMapper.findProjectByGroupOwner(groupId);
    }

    @Override
    @Transactional
    public Pageable<Project> findProjectPageableOfGroup(Integer groupId, String prefix, String keyword,
                                                        int page, int pageSize) {
        List<Integer> groupIdList = Collections.singletonList(groupId);
        return projectMapper.findProjectPageable(null, groupIdList, prefix, keyword, page, pageSize);
    }

    @Override
    @Transactional
    public long countProjectOfGroup(Integer groupId) {
        return projectMapper.countProjectOfGroup(groupId);
    }

    @Override
    @Transactional
    public void updateModelArtifact(User operator, Integer projectId, ModelArtifact modelArtifact) {
        modelArtifact.setProjectId(projectId);
        Project project = projectMapper.findById(projectId);
        modelArtifactService.upload(modelArtifact);
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectId);
        List<EditAnswerAction> actionList = modelInsightService.insight(project, questionnaireVersion, modelArtifact);
        questionnaireService.editAnswer(operator, project, actionList, modelArtifact);
    }

    private void exceptionHandler(PersistenceException exception, Project project) {
        if (PersistenceExceptionUtils.isUniqueConstraintException(exception)) {
            log.warn("exception message: {}", exception.getMessage(), exception);
            if (project.isPersonProject()) {
                User user = userMapper.findById(project.getUserOwnerId());
                throw new DuplicateException(
                        String.format("The project name '%s' has already been used for '%s@%s'.",
                                project.getName(), user.getFullName(), user.getUsername()),
                        exception);
            } else {
                Group group = groupService.findGroupById(project.getGroupOwnerId());
                throw new DuplicateException(
                        String.format("The project name '%s' has already been used in group '%s'.",
                                project.getName(), group.getName()),
                        exception);
            }
        } else {
            throw exception;
        }
    }


}
