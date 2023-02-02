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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.UserDetailDtoConverter;
import org.veritas.assessment.biz.dto.UserChangePasswordDto;
import org.veritas.assessment.biz.dto.UserDetailDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.exception.PermissionException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;
import java.util.Objects;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailDtoConverter userDetailDtoConverter;

    @GetMapping("")
    public UserDetailDto whoami(User operator) throws Exception {
        if (operator == null) {
            throw new NotFoundException("Not found user.");
        }
        return userDetailDtoConverter.convertFrom(operator);
    }

    @PostMapping("")
    public UserDetailDto modifySelf(User operator, @Valid @RequestBody UserSimpleDto userDto) throws Exception {
        if (userDto.getId() == null) {
            userDto.setId(operator.getId());
        }
        if (!Objects.equals(operator.getId(), userDto.getId())) {
            throw new PermissionException("Cannot change other's information.");
        }
        User user = userDto.toEntity();
        user = userService.modify(operator, user);
        return userDetailDtoConverter.convertFrom(user);

    }

    @PostMapping("/change_password")
    public UserDetailDto changePassword(User operator, @Valid @RequestBody UserChangePasswordDto dto) throws Exception {
        if (dto.getId() == null) {
            dto.setId(operator.getId());
        }
        if (!Objects.equals(operator.getId(), dto.getId())) {
            throw new PermissionException("Cannot change other's information.");
        }
        if (StringUtils.equals(dto.getNewPassword(), dto.getOldPassword())) {
            throw new ErrorParamException("The new password should not be same as the old.");
        }
        User user = userService.changePassword(operator, dto.getOldPassword(), dto.getNewPassword());
        return userDetailDtoConverter.convertFrom(user);
    }

    @RequestMapping(path = "/finish_user_guide", method = {RequestMethod.PUT, RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.OK)
    public void finishUserGuide(User operator) {
        userService.finishUserGuide(operator);
    }

}
