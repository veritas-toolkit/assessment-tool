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

package org.veritas.assessment.system.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.veritas.assessment.common.exception.IllegalDataException;
import org.veritas.assessment.system.entity.User;

import java.util.Objects;

@Service("userDetailsService")
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, LockedException {
        User user = userService.findUserByUsernameOrEmail(username);
        if (user == null) {
            log.warn("username [{}] is not found.", username);
            throw new UsernameNotFoundException("username is not found.");
        }
        if (user.isLocked()) {
            throw new LockedException("The account is locked.");
        }
        String password = user.getPassword();
        if (StringUtils.isEmpty(password)) {
            log.error("The user [{}] has no a encoding password.", username);
            throw new IllegalDataException("The user's status false.");
        }
        UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(user.getId().toString()) // Yes, use the user's id to set the 'username' property.
                .password(password)
                .roles(this.roleList(user))
                .build();
        return userDetails;
    }

    private String[] roleList(User user) {
        Objects.requireNonNull(user, "Parameter [user] cannot be null.");
        final String ROLE_ADMIN = "ADMIN";
        final String ROLE_USER = "USER";
        final String[] adminRoleList = {ROLE_ADMIN, ROLE_USER};
        final String[] roleList = {ROLE_USER};
        if (user.isAdmin()) {
            return adminRoleList;
        } else {
            return roleList;
        }
    }
}
