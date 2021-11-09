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

import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.List;


public interface UserService {
    User findUserByUsernameOrEmail(String usernameOrEmail);


    User findUserById(int id);


    List<User> findUserListByPrefix(String prefix);


    List<User> findAllUser();

    Pageable<User> findUserPageable(String prefix, String keyword, int page, int pageSize);

    void successLogin(String usernameOrEmail);

    void failLogin(String usernameOrEmail);

    User register(User user);

    List<User> createUserList(List<User> list);

    void delete(Integer userId);

    User modify(User oldUser, User newUser);

    User changePassword(User user, String oldRawPassword, String newRawPassword);

    User modifyByAdmin(User user);

    User lock(Integer userId);

    User unlock(Integer userId);

    User grantAdminPrivileges(Integer userId);

    User withdrawAdminPrivileges(Integer userId);

    User changePasswordByAdmin(User user, String newRawPassword);
}
