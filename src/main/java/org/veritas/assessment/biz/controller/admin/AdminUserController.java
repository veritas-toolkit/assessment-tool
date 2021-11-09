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
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.veritas.assessment.biz.converter.ProjectDtoConverter;
import org.veritas.assessment.biz.dto.AdminChangePasswordDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.UserAdminModifyDto;
import org.veritas.assessment.biz.dto.UserCreateDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@RestController
@RequestMapping(path = "/admin/user")
@Slf4j
@Validated
public class AdminUserController {
    private static final String DEFAULT_PAGE = "1";
    private static final String DEFAULT_PAGE_SIZE = "20";
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectDtoConverter projectDtoConverter;
    @Autowired
    private GroupService groupService;

    @GetMapping("")
    Pageable<User> listUser(@RequestParam(name = "page", defaultValue = DEFAULT_PAGE) Integer page,
                            @RequestParam(name = "pageSize", defaultValue = DEFAULT_PAGE_SIZE) Integer pageSize,
                            @RequestParam(name = "prefix", required = false) String prefix,
                            @RequestParam(name = "keyword", required = false) String keyword) {
        return userService.findUserPageable(prefix, keyword, page, pageSize);
    }

    @PutMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> createUserList(@RequestBody List<@Valid UserCreateDto> userDtoList) {
        log.debug("userList size: {}", userDtoList.size());
        List<User> userList = userDtoList.stream().map(UserCreateDto::toEntity).collect(Collectors.toList());
        return userService.createUserList(userList);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable("userId") Integer userId) {
        userService.delete(userId);
    }

    @GetMapping("/{userId}")
    User getUser(@PathVariable("userId") Integer userId) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new NotFoundException("Not found the user by id: " + userId);
        }
        return user;
    }

    @PostMapping("/{userId}")
    User modifyUser(@PathVariable("userId") Integer userId, @Valid @RequestBody UserAdminModifyDto userDto) {
        if (userDto.getId() == null) {
            userDto.setId(userId);
        }
        if (!Objects.equals(userId, userDto.getId())) {
            throw new ErrorParamException("");
        }
        User user = userDto.toEntity();
        return userService.modifyByAdmin(user);
    }

    @Operation(summary = "Lock the user.")
    @PostMapping("/{userId}/lock")
    User lock(@PathVariable("userId") Integer userId) {
        return userService.lock(userId);
    }

    @Operation(summary = "Unlock the user.")
    @PostMapping("/{userId}/unlock")
    User unlock(@PathVariable("userId") Integer userId) {
        return userService.unlock(userId);
    }

    @Operation(summary = "Grant user as an admin.")
    @PutMapping("/{userId}/set-admin")
    User grantAdmin(@PathVariable("userId") Integer userId) {
        return userService.grantAdminPrivileges(userId);
    }

    @Operation(summary = "Withdraw admin privileges")
    @PutMapping("/{userId}/unset-admin")
    User withdrawAdmin(@PathVariable("userId") Integer userId) {
        return userService.withdrawAdminPrivileges(userId);
    }

    @PostMapping("/{userId}/change_password")
    User changeUserPassword(@PathVariable("userId") Integer userId,
                            @RequestBody @Valid AdminChangePasswordDto dto) {
        if (Objects.isNull(dto.getNewPassword())) {
            throw new ErrorParamException("[newPassword] cannot be null.");
        }
        if (dto.getId() == null) {
            dto.setId(userId);
        }
        User user = userService.findUserById(userId);
        return userService.changePasswordByAdmin(user, dto.getNewPassword());
    }

    @GetMapping("/{userId}/project")
    Pageable<ProjectDto> findProjectPageable(@PathVariable("userId") Integer userId,
                                             @RequestParam(name = "prefix", required = false) String prefix,
                                             @RequestParam(name = "keyword", required = false) String keyword,
                                             @RequestParam(name = "page", defaultValue = "1") Integer page,
                                             @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable<Project> pageable = projectService.findProjectPageable(userId, prefix, keyword, page, pageSize);
        return projectDtoConverter.convertFrom(pageable);
    }

    @GetMapping("/{userId}/group")
    Pageable<Group> findGroupPageable(@PathVariable("userId") Integer userId,
                                      @RequestParam(name = "page", defaultValue = "1") Integer page,
                                      @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize,
                                      @RequestParam(name = "prefix", required = false) String prefix,
                                      @RequestParam(name = "keyword", required = false) String keyword) {
        return groupService.findGroupPageable(userId, prefix, keyword, page, pageSize);
    }

}
