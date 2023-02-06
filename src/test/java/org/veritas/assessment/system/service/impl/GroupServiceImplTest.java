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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.LastOwnerRoleException;
import org.veritas.assessment.common.exception.QuotaException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;
import org.veritas.assessment.system.service.GroupService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "/sql/unit_test_user.sql")
class GroupServiceImplTest {
    @Autowired
    private GroupService groupService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserMapper userMapper;

    private User admin;

    private User test1;
    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;

    @BeforeEach
    public void init() {
        this.admin = userMapper.findByUsername("admin");
        this.test1 = userMapper.findByUsername("test_1");
        assertNotNull(this.admin);
        assertNotNull(this.test1);

        for(String name : cacheManager.getCacheNames()){
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Test
    public void testCreateGroup_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        Group g = groupService.createGroup(admin, group);
        assertNotNull(g);
        assertEquals(group.getName(), g.getName());
        assertEquals(group.getDescription(), g.getDescription());

        log.info("group: {}", group);
    }

    @Test
    void testCreateGroup_outOfLimitedFail() {
        for (int i = 1; i <= admin.getGroupLimited(); i++) {
            Group group = new Group();
            group.setName("name_" + i);
            group.setDescription("description.");
            groupService.createGroup(admin, group);
            assertNotNull(group.getCreatedTime());
            assertNotNull(group.getCreatorUserId());
        }
        Group group = new Group();
        group.setName("name_" + Integer.MAX_VALUE);
        group.setDescription("description.");
        assertThrows(QuotaException.class, () -> groupService.createGroup(admin, group));
    }

    @Test
    public void testCreateGroup_duplicateFail() {

        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        log.info("group: {}", group);

        log.info("===================================");

        Group group2 = new Group();
        group2.setName("test");
        group2.setDescription("test group");
        assertThrows(DuplicateException.class, () -> groupService.createGroup(admin, group2));
        log.info("group2: {}", group2);
    }

    @Test
    void testModifyGroup_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        log.info("group: {}", group);

        log.warn("===================================");
        group.setName("test-new");
        Group g = groupService.modifyGroup(group);
        assertNotNull(g);
        assertEquals(group.getName(), g.getName());
        assertEquals(group.getDescription(), g.getDescription());
        log.info("after: {}", g);
    }

    @Test
    void testModifyGroup_duplicateFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        log.info("group: {}", group);
        log.warn("===================================");
        group = new Group();
        group.setName("test2");
        group.setDescription("test 2 group");
        groupService.createGroup(admin, group);
        log.info("group: {}", group);
        log.warn("===================================");


        group.setName("test");
        Group finalGroup = group;
        assertThrows(DuplicateException.class, () -> groupService.modifyGroup(finalGroup));
    }

    @Test
    void testDelete_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        assertNotNull(group.getId());
        groupService.delete(group.getId(), false);
    }

    @Test
    void testDelete_notExistSuccess() {
        groupService.delete(Integer.MAX_VALUE, true);
    }

    @Test
    void testDelete_withProjectFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Project project = new Project();
        project.setName("project");
        project.setDescription("description");
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(group.getId());
        project.setBusinessScenario(1);
        projectService.createProject(admin, project, templateQuestionnaireService.findByTemplateId(1));

        assertThrows(ErrorParamException.class, () -> {
            groupService.delete(group.getId(), false);
        });
    }

    @Test
    void testDelete_forceSuccess() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Project project = new Project();
        project.setName("project");
        project.setDescription("description");
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(group.getId());
        project.setBusinessScenario(1);
        projectService.createProject(admin, project, templateQuestionnaireService.findByTemplateId(1));

        groupService.delete(group.getId(), true);
    }

    @Test
    public void testRemoveMember_lastOwnerFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        Assertions.assertThrows(LastOwnerRoleException.class,
                () -> groupService.removeMember(admin.getId(), group.getId()));
    }

    @Test
    void testRemoveMember_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Member member = groupService.addMember(test1.getId(), group.getId(), RoleType.DEVELOPER);
        assertNotNull(member);
        List<Member> memberList = groupService.getMemberList(group.getId());
        assertEquals(2, memberList.size());

        groupService.removeMember(test1.getId(), group.getId());
        List<Member> memberList2 = groupService.getMemberList(group.getId());
        assertEquals(1, memberList2.size());
    }



    @Test
    public void testModifyMember_lastOwnerFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        Assertions.assertThrows(LastOwnerRoleException.class,
                () -> groupService.modifyMember(admin.getId(), group.getId(), RoleType.ASSESSOR));
    }

    @Test
    void testModifyMember_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Member member = groupService.addMember(test1.getId(), group.getId(), RoleType.DEVELOPER);
        assertNotNull(member);
        assertEquals(RoleType.DEVELOPER, member.getType());

        Member newMember = groupService.modifyMember(test1.getId(), group.getId(), RoleType.ASSESSOR);
        assertEquals(RoleType.ASSESSOR, newMember.getType());
    }

    @Test
    public void testAddMember_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        Member member = groupService.addMember(test1.getId(), group.getId(), RoleType.DEVELOPER);
        assertNotNull(member);
        assertEquals(test1.getId(), member.getUserId());
        assertEquals(RoleType.DEVELOPER, member.getType());
    }

    @Test
    public void testAddMember_lastOwnerFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);
        Assertions.assertThrows(LastOwnerRoleException.class,
                () -> groupService.addMember(admin.getId(), group.getId(), RoleType.DEVELOPER));
    }

    private void createSomeGroups() {
        for (int i = 1; i <= 10; i++) {
            Group group = new Group();
            group.setName("group_admin_" + i);
            group.setDescription("description.");
            groupService.createGroup(admin, group);
            assertNotNull(group.getCreatedTime());
            assertNotNull(group.getCreatorUserId());
        }
        for (int i = 1; i <= 10; i++) {
            Group group = new Group();
            group.setName("group_test_" + i);
            group.setDescription("description.");
            groupService.createGroup(test1, group);
            assertNotNull(group.getCreatedTime());
            assertNotNull(group.getCreatorUserId());
        }
    }

    @Test
    void testFindGroupPageable_success() {
        createSomeGroups();
        Pageable<Group> pageable = groupService.findGroupPageable(admin.getId(),
                null, "admin", 1, 10);
        assertEquals(10, pageable.getTotal());
        assertEquals(10, pageable.getRecords().size());
    }

    @Test
    void testFindGroupPageable_noUserSuccess() {
        createSomeGroups();
        Pageable<Group> pageable = groupService.findGroupPageable(
                null, "_1", 1, 10);
        // admin_1, admin_10, test_1, test_10
        assertEquals(4, pageable.getTotal());
        assertEquals(4, pageable.getRecords().size());
    }

    @Test
    void testFindGroupList() {
        createSomeGroups();
        List<Group> list = groupService.findGroupList(admin, null, "admin_1");
        assertEquals(2, list.size());
    }

    @Test
    void testFindGroupListByOwner() {
        createSomeGroups();
        List<Group> list = groupService.findGroupListByOwner(admin, null, "admin_2");
        assertEquals(1, list.size());
    }

    @Test
    void testFindGroupListByCreator() {
        createSomeGroups();
        Pageable<Group> pageable =
                groupService.findGroupListByCreator(test1, null, "test_1", 1, 20);
        assertEquals(2, pageable.getTotal());
        assertEquals(2, pageable.getRecords().size());
    }
}