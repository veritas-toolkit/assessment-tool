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

package org.veritas.assessment.system.service.impl;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.util.PersistenceExceptionUtils;
import org.veritas.assessment.common.exception.DeleteUserException;
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.InternalException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.exception.UserSecurityException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.config.VeritasProperties;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;
import org.veritas.assessment.system.service.RoleService;
import org.veritas.assessment.system.service.UserService;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VeritasProperties veritasProperties;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    @Getter
    private PasswordEncoder passwordEncoder;


    public User findUserByUsernameOrEmail(String usernameOrEmail) {
        return userMapper.findByUsernameOrEmail(usernameOrEmail);
    }

    public User findUserById(int id) {
        return userMapper.findById(id);
    }

    public User findUserByName(String username) {
        return userMapper.findByUsername(username);
    }

    public User findUserEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public List<User> findUserListByPrefix(String prefix) {
        // check prefix length
        return userMapper.findByFuzzyPrefix(prefix);
    }

    public List<User> findAllUser() {
        return userMapper.findAll();
    }

    @Override
    public Pageable<User> findUserPageable(String prefix, String keyword, int page, int pageSize) {
        return userMapper.findPageable(prefix, keyword, page, pageSize);
    }

    public void successLogin(String usernameOrEmail) {
        User user = userMapper.findByUsernameOrEmail(usernameOrEmail);
        user.loginSuccess();
        userMapper.updateLastLoginTime(user);
    }

    public void failLogin(String usernameOrEmail) {
        log.warn("fail login: {}", usernameOrEmail);
        User user = userMapper.findByUsernameOrEmail(usernameOrEmail);
        if (user == null) {
            return;
        }
        user.loginAttemptIncrease();
        if (!user.isLocked()) {
            boolean locked = user.getLoginAttempt() >= veritasProperties.getMaxFailLoginAttempt();
            if (locked) {
                log.warn("The user{} fail login too much, lock it.", user.getId());
            }
            user.setLocked(locked);
        }
        userMapper.failLogin(user);
    }

    /**
     * User register.
     * <li>
     *     <ol>check username. cannot be all number</ol>
     *     <ol>check email is a valid email address</ol>
     *
     * </li>
     *
     * @param user
     * @return
     */
    public User register(User user) {
        User temp = new User();
        temp.setUsername(user.getUsername());
        temp.setFullName(user.getFullName());
        temp.setEmail(user.getEmail());
        temp.setPassword(passwordEncoder.encode(user.getPassword()));
        temp.setLocked(false);
        temp.setLoginAttempt(0);
        temp.setGroupLimited(veritasProperties.getDefaultGroupLimited());
        temp.setProjectLimited(veritasProperties.getDefaultProjectLimited());
        temp.setCreatedTime(new Date());
        temp.setLastLoginTime(null);
        temp.setShouldChangePassword(false);
        try {
            userMapper.addUser(temp);
        } catch (Exception exception) {
            exceptionHandler(exception, null, user.getUsername(), user.getEmail());
        }
        return temp;
    }

    @Override
    public List<User> createUserList(List<User> list) {
        if (list == null) {
            return Collections.emptyList();
        }
        Date now = new Date();
        for (User user : list) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setLocked(false);
            user.setLoginAttempt(0);
            if (user.getGroupLimited() == null) {
                user.setGroupLimited(veritasProperties.getDefaultGroupLimited());
            }
            if (user.getProjectLimited() == null) {
                user.setProjectLimited(veritasProperties.getDefaultProjectLimited());
            }
            if (user.isAdmin()) {
//                user.setAdmin(false);
                log.info("Create a admin user, username: {}, fullname: {}", user.getUsername(), user.getFullName());
            }
            user.setCreatedTime(now);
            user.setLastLoginTime(null);
            try {
                userMapper.addUser(user);
//            } catch (PersistenceException exception) {
            } catch (Exception exception) {
                exceptionHandler(exception, null, user.getUsername(), user.getEmail());
            }
        }
        return list;
    }

    @Override
    public void delete(Integer userId) {
        ErrorParamException.requireNonNull(userId, "user id should not be null.");
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("Not found user:" + userId);
        }
        List<Project> projectList = projectService.findByUserOwnerId(user.getId());
        if (projectList != null && !projectList.isEmpty()) {
            if (projectList.size() == 1) {
                throw new DeleteUserException("The user still has one project.");
            } else {
                throw new DeleteUserException(String.format("The user still has %d projects.", projectList.size()));
            }
        }
        int result = userMapper.delete(user);
        if (result != 1) {
            log.warn("Lock user may failed. The result is: {}", result);
        } else {
            roleService.removeRoleByUser(userId);
        }
    }

    public User modify(User oldUser, User newUser) {
        Objects.requireNonNull(oldUser);
        Objects.requireNonNull(newUser);
        if (!Objects.equals(oldUser.getId(), newUser.getId())) {
            throw new IllegalArgumentException("Old and new user object are not the same user.");
        }
        try {
            userMapper.modify(oldUser, newUser);
        } catch (Exception exception) {
            exceptionHandler(exception, newUser.getId(), newUser.getUsername(), newUser.getEmail());
        }
        return userMapper.findById(newUser.getId());
    }

    public User changePassword(User user, String oldRawPassword, String newRawPassword) {
        if (!this.getPasswordEncoder().matches(oldRawPassword, user.getPassword())) {
            throw new UserSecurityException("Password valid failed.");
        }
        String newEncodedPassword = this.getPasswordEncoder().encode(newRawPassword);
        int result = userMapper.changePassword(user, newEncodedPassword);
        if (result <= 0) {
            throw new UserSecurityException("Changing password failed.");
        }
        return userMapper.findById(user.getId());
    }


    @Transactional(rollbackFor = Exception.class)
    public User modifyByAdmin(User user) {
        int result = 0;
        try {
            result = userMapper.modifyByAdmin(user);
        } catch (Exception exception) {
            exceptionHandler(exception, user.getId(), user.getUsername(), user.getEmail());
        }
        if (result != 1) {
            log.warn("Update user[{}] failed.", user.getId());
        }
        return userMapper.findById(user.getId());
    }

    @Override
    public User lock(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("The user is not found.");
        }
        if (!user.isLocked()) {
            userMapper.lock(user);
        }
        return user;
    }

    @Override
    public User unlock(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("The user is not found.");
        }
        if (user.isLocked()) {
            userMapper.unlock(user);
        }
        return user;
    }

    @Override
    @Transactional
    public User grantAdminPrivileges(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("The user is not found.");
        }
        if (!user.isAdmin()) {
            userMapper.grantAdminPrivileges(user);
        }
        return user;
    }

    @Override
    @Transactional
    public User withdrawAdminPrivileges(Integer userId) {
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("The user is not found.");
        }
        if (user.isAdmin()) {
            userMapper.withdrawAdminPrivileges(user);
        }
        return user;
    }

    public User changePasswordByAdmin(User user, String newRawPassword) {
        String newEncodedPassword = this.getPasswordEncoder().encode(newRawPassword);
        int result = userMapper.changePasswordByAdmin(user, newEncodedPassword);
        if (result <= 0) {
            throw new UserSecurityException("Changing password failed.");
        }
        return userMapper.findById(user.getId());
    }

    @Override
    public void finishUserGuide(User user) {
        int result = userMapper.finishUserGuide(user);
    }

    private void exceptionHandler(Exception exception, Integer oldId, String username, String email) {
        if (PersistenceExceptionUtils.isUniqueConstraintException(exception)) {
            User user = null;
            User userByUsername = null;
            User userByEmail = null;
            if (oldId != null) {
                user = userMapper.findById(oldId);
            }
            if (StringUtils.isNotEmpty(username)) {
                userByUsername = userMapper.findByUsernameOrEmail(username);
            }
            if (StringUtils.isNotEmpty(email)) {
                userByEmail = userMapper.findByUsernameOrEmail(email);
            }

            if (user != null) {
                if (userByUsername != null && !Objects.equals(userByUsername.getId(), user.getId())) {
                    throw new DuplicateException(String.format("The username[%s] has already been used.", username));
                }
                if (userByEmail != null && !Objects.equals(userByEmail.getId(), user.getId())) {
                    throw new DuplicateException(String.format("The email[%s] has already been used.", email));
                }
            } else {
                if (userByUsername != null) {
                    throw new DuplicateException(String.format("The username[%s] has already been used.", username));
                }
                if (userByEmail != null) {
                    throw new DuplicateException(String.format("The email[%s] has already been used.", email));
                }
            }
        } else {
            throw new InternalException("Add or update user info failed.", exception);
        }
    }
}
