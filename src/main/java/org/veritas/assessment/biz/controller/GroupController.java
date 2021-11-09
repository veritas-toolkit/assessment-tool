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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.veritas.assessment.biz.converter.GroupDtoConverter;
import org.veritas.assessment.biz.converter.ProjectDtoConverter;
import org.veritas.assessment.biz.dto.GroupDetailDto;
import org.veritas.assessment.biz.dto.GroupDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.RoleDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.dto.GroupCreateDto;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.RoleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectDtoConverter projectDtoConverter;

    @Autowired
    private GroupDtoConverter groupDtoConverter;

    @Operation(summary = "Find current user can access group pageable list")
    @GetMapping("")
    public Pageable<GroupDto> listGroup(
            @Parameter(hidden = true) User operator,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword) {
        Pageable<Group> pageable = groupService.findGroupPageable(operator, prefix, keyword, page, pageSize);
        return groupDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Find current user can access all groups")
    @GetMapping("/all")
    public List<GroupDto> listGroup(@Parameter(hidden = true) User operator,
                                    @RequestParam(name = "prefix", required = false) String prefix,
                                    @RequestParam(name = "keyword", required = false) String keyword) {
        List<Group> groupList = groupService.findGroupList(operator, prefix, keyword);
        return groupDtoConverter.convertFrom(groupList);
    }

    @Operation(summary = "Find group created by current user pageable list")
    @GetMapping("/my-groups")
    public Pageable<GroupDto> listGroupCreatedByMe(
            @Parameter(hidden = true) User operator,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword) {
        Pageable<Group> pageable = groupService.findGroupListByCreator(operator, prefix, keyword, page, pageSize);
        return groupDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Find group owned by current user pageable list")
    @GetMapping("/owned-by-me")
    public List<GroupDto> findGroupListByOwner(
            @Parameter(hidden = true) User operator,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword) {
        List<Group> groupList = groupService.findGroupListByOwner(operator, prefix, keyword);
        return groupDtoConverter.convertFrom(groupList);
    }

    @Operation(summary = "Create group")
    @PutMapping("/new")
    @ResponseStatus(code = HttpStatus.CREATED)
    public GroupDto createGroup(@Parameter(hidden = true) User operator,
                                @RequestBody GroupCreateDto dto) {
        Group group = dto.toEntity(operator.getId());
        group = groupService.createGroup(operator, group);
        return groupDtoConverter.convertFrom(group);
    }

    @Operation(summary = "Delete group")
    @DeleteMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'group', 'delete')")
    public void deleteGroup(@PathVariable("groupId") Integer groupId,
                            @RequestParam(name = "force", required = false, defaultValue = "false") boolean force) {
        groupService.delete(groupId, force);
    }

    @Operation(summary = "Modify group")
    @PostMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'group', 'edit')")
    public GroupDto modifyGroup(@Parameter(hidden = true) User operator,
                                @PathVariable("groupId") Integer groupId,
                                @RequestBody Group group) {
        if (group.getId() == null) {
            group.setId(groupId);
        } else if (!Objects.equals(groupId, group.getId())) {
            throw new ErrorParamException(String.format("group id[%d] is not same as path value[%d]",
                    group.getId(), groupId));
        }
        group = groupService.modifyGroup(group);
        return groupDtoConverter.convertFrom(group);
    }

    /**
     * deprecated
     *
     * @param operator operator user
     * @param groupId  id of group
     * @return group
     * @deprecated see {@link #getGroupDetail(User, int)}
     */
    @Operation(summary = "Get the group by id.")
    @GetMapping("/{groupId}")
    @PreAuthorize("hasPermission(#groupId, 'group', 'read')")
    public GroupDto getGroup(@Parameter(hidden = true) User operator,
                             @PathVariable("groupId") int groupId) {
        Group group = groupService.findGroupById(groupId);
        return groupDtoConverter.convertFrom(group);
    }

    @Operation(summary = "Get the group detail by id.")
    @GetMapping("/{groupId}/detail")
    @PreAuthorize("hasPermission(#groupId, 'group', 'read')")
    public GroupDetailDto getGroupDetail(User operator, @PathVariable("groupId") int groupId) {
        Group group = groupService.findGroupById(groupId);
        if (group == null) {
            throw new NotFoundException("Not found the group");
        }
        GroupDetailDto dto = new GroupDetailDto();
        dto.setGroup(group);
        UserRole userRole = roleService.findUserRole(operator.getId(), ResourceType.GROUP, groupId);
        RoleDto roleDto = new RoleDto(userRole.getRole());
        dto.setGroupRole(roleDto);
        return dto;
    }

    @Operation(summary = "Get pageable projects of the group")
    @PreAuthorize("hasPermission(#groupId, 'group', 'read')")
    @GetMapping("/{groupId}/project")
    public Pageable<ProjectDto> listProject(@PathVariable(name = "groupId") Integer groupId,
                                            @RequestParam(name = "prefix", required = false) String prefix,
                                            @RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                                            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable<Project> pageable = projectService.findProjectPageableOfGroup(groupId, prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Get member list of the group.")
    @PreAuthorize("hasPermission(#groupId, 'group', 'read')")
    @GetMapping("/{groupId}/member")
    public List<Member> listMember(@PathVariable("groupId") Integer groupId) {
        return groupService.getMemberList(groupId);
    }

    @Operation(summary = "Invite a member to the group.")
    @PutMapping("/{groupId}/member")
    @PreAuthorize("hasPermission(#groupId, 'group', 'manage members')")
    public List<Member> addMemberList(@PathVariable("groupId") Integer groupId,
                                      @RequestBody List<MembershipDto> dtoList) {
        dtoList.forEach(dto -> {
            if (!dto.valid()) {
                throw new ErrorParamException("Params [userId, type] can't be null");
            }
        });
        List<Member> memberList = new ArrayList<>();
        dtoList.forEach(dto -> {
            Member member = groupService.addMember(dto.getUserId(), groupId, dto.getType());
            if (member != null) {
                memberList.add(member);
            } else {
                log.warn("Add user[{}} to group[{}] failed.", dto.getUserId(), groupId);
            }
        });
        return memberList;
    }

    @Operation(summary = "Delete a member from the group.")
    @DeleteMapping("/{groupId}/member/{userId}")
    @PreAuthorize("hasPermission(#groupId, 'group', 'manage members')")
    public void deleteMember(@Parameter(hidden = true) User operator,
                             @PathVariable("groupId") Integer groupId,
                             @PathVariable("userId") Integer userId) {
        if (userId == null) {
            throw new ErrorParamException("Params [userId] cannot be null");
        }
        groupService.removeMember(userId, groupId);
    }

    @Operation(summary = "Modify a member of the group.")
    @PreAuthorize("hasPermission(#groupId, 'group', 'manage members')")
    @PostMapping("/{groupId}/member")
    public Member modifyMember(@PathVariable("groupId") Integer groupId,
                               @RequestBody MembershipDto membershipDto) {
        if (!membershipDto.valid()) {
            throw new ErrorParamException("Params [userId, type] can't be null");
        }
        return groupService.modifyMember(membershipDto.getUserId(), groupId, membershipDto.getType());
    }
}
