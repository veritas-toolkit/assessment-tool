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

import org.veritas.assessment.system.entity.User;

public interface AdminService {
    /**
     * Create a new user.
     *
     * @param user user to be created.
     * @return created user object.
     */
    User createUser(User user);

    User adminUser(User user);

    /**
     * Grant a role to user.
     *
     * @param user   user to be granted.
     * @param roleId role id.
     */
    void grantRole(User user, int roleId);


}
