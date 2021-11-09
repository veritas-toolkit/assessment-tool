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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.util.PersistenceExceptionUtils;
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.LastOwnerRoleException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.exception.PermissionException;
import org.veritas.assessment.common.exception.QuotaException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleEnum;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.GroupMapper;
import org.veritas.assessment.system.mapper.UserMapper;
import org.veritas.assessment.system.rbac.Role;
import org.veritas.assessment.system.rbac.UserRole;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.RoleService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ProjectService projectService;

    @Override
    @Transactional
    public Group createGroup(User operator, Group group) {
        int operatorId = operator.getId();
        int created = groupMapper.numberOfGroupCreatedByUser(operatorId);
        if (created >= operator.getGroupLimited()) {
            throw new QuotaException("Your quota of creating group is " +
                    operator.getGroupLimited() +
                    ", and can't create more group.");
        }
        group.setCreatorUserId(operatorId);
        Date now = new Date();
        group.setCreatedTime(now);
        group.setLastModifiedTime(now);
        try {
            groupMapper.addGroup(group);
        } catch (PersistenceException exception) {
            exceptionHandler(exception, group.getName());
        }

        this.addMemberInternal(operator, group.getId(), RoleType.OWNER);
        return group;
    }

    @Override
    public void delete(int groupId, boolean force) {
        Group group = groupMapper.findById(groupId);
        if (group == null) {
            log.warn("Group[{}] does not exist", groupId);
            return;
        }
        if (!force) {
            int projectCount = projectService.countProjectOfGroup(groupId);
            if (projectCount > 0) {
                throw new ErrorParamException("The group has projects. You'd better not delete it." +
                        "Or use force delete, it will cause all projects of group to be deleted.");
            }
        } else {
            projectService.deleteProjectByGroupOwner(groupId);
        }
        groupMapper.delete(group);
        roleService.removeRoleByResource(ResourceType.GROUP, groupId);
        log.info("Delete group: {}", group);
    }

    @Override
    @Transactional
    public Group findGroupById(int groupId) throws PermissionException {
        Group group = this.groupMapper.findById(groupId);
        if (group == null) {
            throw new NotFoundException(String.format("Group [%d] is not found.", groupId));
        }
        return group;
    }

    @Override
    @Transactional
    public Group modifyGroup(Group group) {
        Group old = groupMapper.findById(group.getId());
        group.setLastModifiedTime(new Date());
        try {
            groupMapper.update(old, group);
        } catch (PersistenceException exception) {
            exceptionHandler(exception, group.getName());
        }
        return groupMapper.findById(group.getId());
    }

    @Override
    @Transactional
    public Member addMember(Integer userId, Integer groupId, RoleType roleType) {
        this.removeMember(userId, groupId);
        User user = userMapper.findById(userId);
        if (user == null) {
            throw new NotFoundException("Not found the user:" + userId);
        }

        UserRole userRole = roleService.grantRole(userId, ResourceType.GROUP, groupId, roleType);
        return new Member(user, userRole);
    }

    // remove a member from the group
    @Override
    @Transactional
    public int removeMember(int userId, int groupId) {
        UserRole userRole = roleService.findUserRole(userId, ResourceType.GROUP, groupId);
        if (userRole == null) {
            log.warn("Group[{}] does not has member, id: {}", groupId, userId);
            return 0;
        } else {
            Role role = userRole.getRole();
            if (role != null) {
                if (role.isTypeOf(RoleType.OWNER)) {
                    List<UserRole> userRoleList = roleService.findByResource(ResourceType.GROUP, groupId);
                    long ownerCount = userRoleList.stream().filter(e -> e.roleType() == RoleType.OWNER).count();
                    if (ownerCount == 1) {
                        throw new LastOwnerRoleException("The group need at last one owner.");
                    }
                }
            }
        }
        int result = roleService.removeRole(userId, ResourceType.GROUP, groupId);
        if (result <= 0) {
            log.warn("Remove user[{}] from group[{}] failed", userId, groupId);
        }
        return result;
    }


    @Override
    @Transactional
    public Member modifyMember(Integer userId, Integer groupId, RoleType roleType) {
        User user = userMapper.findById(userId);
        int result = this.removeMember(userId, groupId);
        if (result <= 0) {
            log.warn("Removing user[{}] from group[{}] return {} (<=0).", userId, groupId, result);
        }
        return this.addMemberInternal(user, groupId, roleType);
    }

    @Override
    @Transactional
    public List<Member> getMemberList(Integer groupId) {
        List<UserRole> userRoleList = roleService.findByResource(ResourceType.GROUP, groupId);
        List<Member> memberList = new ArrayList<>(userRoleList.size());
        userRoleList.forEach(userRole -> {
            User user = userMapper.findById(userRole.getUserId());
            Member member = new Member(user, userRole);
            memberList.add(member);
        });
        return memberList;
    }


    private Member addMemberInternal(User user, Integer groupId, RoleType roleType) {
        UserRole userRole = roleService.grantRole(user.getId(), ResourceType.GROUP, groupId, roleType);
        return new Member(user, userRole);
    }


    @Override
    @Transactional
    public Pageable<Group> findGroupPageable(Integer userId, String prefix, String keyword, int page, int pageSize) {
        List<UserRole> userRoleList = roleService.findResourceByUser(userId, ResourceType.GROUP);
        List<Integer> groupIdList = userRoleList.stream().map(UserRole::getResourceId).collect(Collectors.toList());
        return groupMapper.findGroupListPageable(groupIdList, prefix, keyword, page, pageSize);
    }

    @Override
    @Transactional
    @Deprecated
    public Pageable<Group> findGroupPageable(String prefix, String keyword, int page, int pageSize) {
        return groupMapper.findGroupPageable(prefix, keyword, page, pageSize);
    }

    @Override
    @Transactional
    public List<Group> findGroupList(User user, String prefix, String keyword) {
        int userId = user.getId();
        List<UserRole> userRoleList = roleService.findResourceByUser(userId, ResourceType.GROUP);
        List<Integer> groupIdList = userRoleList.stream().map(UserRole::getResourceId).collect(Collectors.toList());
        return groupMapper.findGroupList(groupIdList, prefix, keyword);
    }

    @Override
    @Transactional
    public List<Group> findGroupListByOwner(User owner, String prefix, String keyword) {
        int userId = owner.getId();
        List<UserRole> userRoleList = roleService.findResourceByUser(userId, ResourceType.GROUP);
        List<Integer> groupIdList = userRoleList
                .stream()
                .filter(userRole -> {
                    Role role = userRole.getRole();
                    if (role == null) {
                        return false;
                    } else {
                        return StringUtils.equals(role.getName(), RoleEnum.GROUP_OWNER.getName());
                    }
                })
                .map(UserRole::getResourceId).collect(Collectors.toList());
        return groupMapper.findGroupList(groupIdList, prefix, keyword);
    }

    @Override
    @Transactional
    public Pageable<Group> findGroupListByCreator(User creator, String prefix, String keyword, int page, int pageSize) {
        return groupMapper.findGroupListPageableByCreator(creator.getId(), prefix, keyword, page, pageSize);
    }


    private void exceptionHandler(PersistenceException exception, String groupName) {
        if (PersistenceExceptionUtils.isUniqueConstraintException(exception)) {
            log.warn("exception message: {}", exception.getMessage(), exception);
            throw new DuplicateException(
                    String.format("The group name '%s' has already been used.", groupName),
                    exception);
        } else {
            throw exception;
        }
    }
}
