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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.dto.UserRegisterDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping("/user/{id}")
    public UserSimpleDto findUserById(@PathVariable("id") Integer id) throws Exception {
        User user = userService.findUserById(id);
        if (user == null) {
            throw new NotFoundException("Not found user id is " + id);
        }
        return new UserSimpleDto(user);
    }

    @GetMapping("/user")
    public List<UserSimpleDto> findByPrefix(@RequestParam(name = "prefix", required = false) String prefix) throws Exception {
        List<User> userList;
        if (StringUtils.isNotEmpty(prefix)) {
            userList = userService.findUserListByPrefix(prefix);
        } else {
            userList = userService.findAllUser();
        }
        if (userList == null || userList.isEmpty()) {
            throw new NotFoundException("Not found user.");
        } else {
            return UserSimpleDto.fromUserList(userList);
        }
    }

    @PutMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public User register(@Valid @RequestBody UserRegisterDto userDto) throws Exception {
        User user = userDto.toEntity();
        user.setAdmin(false);
        return userService.register(user);
    }
}
