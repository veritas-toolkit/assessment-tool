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
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;

import java.util.List;

public interface GroupService {
    Group createGroup(User operator, Group group);

    default void delete(int groupId) {
        delete(groupId, false);
    }

    void delete(int groupId, boolean force);

    Group modifyGroup(Group group);

    Group findGroupById(int gid);

    Member addMember(Integer userId, Integer groupId, RoleType roleType);

    int removeMember(int userId, int groupId);

    Member modifyMember(Integer userId, Integer groupId, RoleType roleType);

    List<Member> getMemberList(Integer groupId);

    /**
     * Find the group list pageable witch the user can access.
     *
     * @param userId   The user
     * @param prefix   group's name prefix
     * @param keyword  group's name keyword
     * @param page     current page number
     * @param pageSize page size
     * @return The group pageable result
     */
    Pageable<Group> findGroupPageable(Integer userId, String prefix, String keyword, int page, int pageSize);

    default Pageable<Group> findGroupPageable(User operator, String prefix, String keyword, int page, int pageSize) {
        return findGroupPageable(operator.getId(), prefix, keyword, page, pageSize);
    }

    Pageable<Group> findGroupPageable(String prefix, String keyword, int page, int pageSize);

    /**
     * Find the group list witch the user can access.
     *
     * @param user    The user who access the group.
     * @param prefix  group's name prefix
     * @param keyword group's name keyword
     * @return The group list
     */
    List<Group> findGroupList(User user, String prefix, String keyword);

    List<Group> findGroupListByOwner(User owner, String prefix, String keyword);

    Pageable<Group> findGroupListByCreator(User creator, String prefix, String keyword, int page, int pageSize);

}
