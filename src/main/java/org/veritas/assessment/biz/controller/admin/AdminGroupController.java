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
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping(path = "/admin/group")
@Slf4j
public class AdminGroupController {
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_PAGE_SIZE = "20";
    @Autowired
    private UserService userService;
    @Autowired
    private GroupService groupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectDtoConverter projectDtoConverter;

    // list group
    @GetMapping("")
    public Pageable<Group> listGroup(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page,
            @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword) {
        return groupService.findGroupPageable(prefix, keyword, page, pageSize);
    }

    @GetMapping("/{groupId}")
    public Group findGroupById(@PathVariable("groupId") Integer groupId) {
        return groupService.findGroupById(groupId);
    }

    @DeleteMapping("/{groupId}")
    public void deleteGroup(@PathVariable("groupId") Integer groupId,
                            @RequestParam(name = "force", required = false, defaultValue = "false") boolean force) {
        groupService.delete(groupId, force);
    }

    @PostMapping("/{groupId}")
    public Group modifyGroup(User operator,
                             @PathVariable("groupId") Integer groupId,
                             @RequestBody Group group) {
        if (group.getId() == null) {
            group.setId(groupId);
        } else if (!Objects.equals(groupId, group.getId())) {
            throw new ErrorParamException(String.format("group id[%d] is not same as path value[%d]",
                    group.getId(), groupId));
        }
        return groupService.modifyGroup(group);
    }


    @Operation(summary = "Get pageable projects of the group")
    @GetMapping("/{groupId}/project")
    public Pageable<ProjectDto> listProject(@PathVariable(name = "groupId") Integer groupId,
                                            @RequestParam(name = "prefix", required = false) String prefix,
                                            @RequestParam(name = "keyword", required = false) String keyword,
                                            @RequestParam(name = "page", defaultValue = "1") Integer page,
                                            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable<Project> pageable = projectService.findProjectPageableOfGroup(groupId, prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }

    @GetMapping("/{groupId}/member")
    public List<Member> listMember(@PathVariable("groupId") Integer groupId) {
        return groupService.getMemberList(groupId);
    }

    @PutMapping("/{groupId}/member")
    public List<Member> addMemberList(@PathVariable("groupId") Integer groupId,
                                      @Valid @RequestBody List<MembershipDto> dtoList) {
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

    @DeleteMapping("/{groupId}/member/{userId}")
    public void deleteMember(User operator,
                             @PathVariable("groupId") Integer groupId,
                             @PathVariable("userId") Integer userId) {
        if (userId == null) {
            throw new ErrorParamException("Params [userId] cannot be null");
        }
        groupService.removeMember(userId, groupId);
    }

    @PostMapping("/{groupId}/member")
    public Member modifyMember(@PathVariable("groupId") Integer groupId,
                               @RequestBody MembershipDto membershipDto) {
        if (!membershipDto.valid()) {
            throw new ErrorParamException("Params [userId, type] can't be null");
        }
        return groupService.modifyMember(membershipDto.getUserId(), groupId, membershipDto.getType());
    }


}
