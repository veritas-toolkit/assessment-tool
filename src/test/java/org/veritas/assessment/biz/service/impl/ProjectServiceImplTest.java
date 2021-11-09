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

package org.veritas.assessment.biz.service.impl;

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
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.exception.QuotaException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;
import org.veritas.assessment.system.service.GroupService;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
public class ProjectServiceImplTest {

    @Autowired
    GroupService groupService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserMapper userMapper;
    private User admin;
    private Project individualProject;
    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void evictAllCaches() {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @BeforeEach
    public void init() {
        this.admin = userMapper.findByUsername("admin");
        assertNotNull(this.admin);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setUserOwnerId(admin.getId());
        projectService.createProject(admin, project, 1);
        this.individualProject = project;
    }

    @Test
    void testCreate_groupProjectSuccess() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(group.getId());
        projectService.createProject(admin, project, 1);
        assertEquals(1, projectService.countProjectOfGroup(group.getId()));

        List<Project> list = projectService.findByGroupOwnerId(group.getId());
        assertEquals(1, list.size());

        Pageable<Project> pageable = projectService.findProjectPageableOfGroup(group.getId(), null, null, 1, 20);
        assertEquals(1, pageable.getPageCount());
        assertEquals(1, pageable.getTotal());
        assertEquals(project.getId(), pageable.getRecords().get(0).getId());
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testCreate_noGroupFail() throws Exception {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(99999999);
        assertThrows(NotFoundException.class, () -> {
            projectService.createProject(admin, project, 1);
        });

    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testCreate_beyondQuotaFail() throws Exception {
        User test1 = userMapper.findByUsername("test_1");
        for (int i = 1; i <= test1.getProjectLimited(); ++i) {
            Project project = new Project();
            project.setName("test_project_" + i);
            project.setDescription("test_description");
            project.setBusinessScenario(1);
            project.setCreatorUserId(test1.getId());
            project.setUserOwnerId(test1.getId());
            projectService.createProject(test1, project, 1);
        }
        Project project = new Project();
        project.setName("test_project_" + test1.getProjectLimited() + 1);
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(test1.getId());
        project.setUserOwnerId(test1.getId());

        assertThrows(QuotaException.class, () -> projectService.createProject(test1, project, 1));
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testCreate_createOtherOwnerFail() {
        User test1 = userMapper.findByUsername("test_1");
        Project project = new Project();
        project.setName("test_project_" + test1.getProjectLimited() + 1);
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(test1.getId());
        // owner set admin
        project.setUserOwnerId(admin.getId());

        assertThrows(ErrorParamException.class, () -> {
            try {
                projectService.createProject(test1, project, 1);
            } catch (Throwable throwable) {
                log.warn("exception", throwable);
                throw throwable;
            }
        });
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testCreate_noneGroupOwnerFail() {
        User test1 = userMapper.findByUsername("test_1");
        Project project = new Project();
        project.setName("test_project_" + test1.getProjectLimited() + 1);
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(test1.getId());
        // group is not exist
        project.setGroupOwnerId(Integer.MAX_VALUE);

        assertThrows(NotFoundException.class, () -> {
            try {
                projectService.createProject(test1, project, 1);
            } catch (Throwable throwable) {
                log.warn("exception", throwable);
                throw throwable;
            }
        });
    }


    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testCreate_noPermissionFail() throws Exception {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        Project newProject = new Project();
        newProject.setName("test");
        newProject.setDescription("test_description");
        newProject.setBusinessScenario(1);


        User testUser = userMapper.findById(2);
        newProject.setCreatorUserId(testUser.getId());
        newProject.setGroupOwnerId(group.getId());
        log.info("new project:\n{}", newProject);
        log.info("=============================================================");
        assertThrows(ErrorParamException.class, () -> {
            projectService.createProject(testUser, newProject, 1);
        });
        log.info("new project:\n{}", newProject);
    }


    @Test
    void testCreateProject_duplicateFail() {
        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setUserOwnerId(admin.getId());
        assertThrows(DuplicateException.class,
                () -> projectService.createProject(admin, project, 1));
    }

    @Test
    void testCreateProject_duplicateGroupFail() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);


        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(group.getId());
        projectService.createProject(admin, project, 1);

        Project project2 = new Project();
        project2.setName("test");
        project2.setDescription("test_description");
        project2.setBusinessScenario(1);
        project2.setCreatorUserId(admin.getId());
        project2.setGroupOwnerId(group.getId());
        assertThrows(DuplicateException.class,
                () -> projectService.createProject(admin, project2, 1));
    }

    @Test
    void testModifyProject_duplicateFail() {
        Project project = new Project();
        project.setName("test2");
        project.setDescription("test2_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setUserOwnerId(admin.getId());
        projectService.createProject(admin, project, 1);

        project.setName("test"); // duplicate
        assertThrows(DuplicateException.class,
                () -> projectService.modifyProject(project));
    }

    @Test
    void testDelete_success() {
        projectService.delete(individualProject.getId());
    }

    @Test
    void testDeleteByUserOwner_success() {
        int result = projectService.deleteProjectByUserOwner(admin.getId());
        assertEquals(1, result);
    }

    @Test
    void testDeleteByGroupOwner_success() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);

        int count = 0;
        for (int i = 1; i <= admin.getProjectLimited() / 2; ++i) {
            Project project = new Project();
            project.setName("test_project_" + i);
            project.setDescription("test_description");
            project.setBusinessScenario(1);
            project.setCreatorUserId(admin.getId());
            project.setGroupOwnerId(group.getId());
            projectService.createProject(admin, project, 1);
            ++count;
        }
        int result = projectService.deleteProjectByGroupOwner(group.getId());
        assertEquals(count, result);
    }

    @Test
    void testFindById_success() {
        Project project = projectService.findProjectById(individualProject.getId());
        assertEquals(individualProject.getName(), project.getName());
        assertEquals(individualProject.getOwnerId(), project.getOwnerId());
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testRemoveMember_success() {
        User test1 = userMapper.findByUsername("test_1");
        projectService.addMember(individualProject.getId(), test1.getId(), RoleType.ASSESSOR);
        projectService.removeMember(individualProject.getId(), test1.getId());
    }

    @Test
    void testRemoveMember_fail() {
        Assertions.assertThrows(ErrorParamException.class,
                () -> projectService.removeMember(individualProject.getId(), admin.getId()));
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testModifyMember_success() {
        User test1 = userMapper.findByUsername("test_1");
        projectService.addMember(individualProject.getId(), test1.getId(), RoleType.ASSESSOR);
        projectService.modifyMember(individualProject.getId(), test1.getId(), RoleType.DEVELOPER);
    }

    @Test
    void testModifyMember() {
        Assertions.assertThrows(ErrorParamException.class,
                () -> projectService.modifyMember(individualProject.getId(), admin.getId(), RoleType.DEVELOPER));

    }

    @Test
    void testAddMember() {
        Assertions.assertThrows(ErrorParamException.class,
                () -> projectService.addMember(individualProject.getId(), admin.getId(), RoleType.DEVELOPER));
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testFindMemberList_success() {
        User test1 = userMapper.findByUsername("test_1");
        projectService.addMember(individualProject.getId(), test1.getId(), RoleType.ASSESSOR);
        User test2 = userMapper.findByUsername("test_2");
        projectService.addMember(individualProject.getId(), test2.getId(), RoleType.DEVELOPER);

        List<Member> memberList = projectService.findMemberList(individualProject.getId());
        log.info("member list: {}", memberList);
        assertEquals(3, memberList.size());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), admin.getId())
                                        && member.getType() == RoleType.OWNER)
                        .count());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), test1.getId())
                                        && member.getType() == RoleType.ASSESSOR)
                        .count());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), test2.getId())
                                        && member.getType() == RoleType.DEVELOPER)
                        .count());

    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testFindMemberList_groupProjectSuccess() {
        Group group = new Group();
        group.setName("test");
        group.setDescription("test group");
        groupService.createGroup(admin, group);


        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
        project.setBusinessScenario(1);
        project.setCreatorUserId(admin.getId());
        project.setGroupOwnerId(group.getId());
        projectService.createProject(admin, project, 1);


        User test1 = userMapper.findByUsername("test_1");
        groupService.addMember(test1.getId(), group.getId(), RoleType.ASSESSOR);

        User test2 = userMapper.findByUsername("test_2");
        projectService.addMember(project.getId(), test2.getId(), RoleType.DEVELOPER);

        List<Member> memberList = projectService.findMemberList(project.getId());
        log.info("member list: {}", memberList);
        assertEquals(3, memberList.size());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), admin.getId())
                                        && member.getType() == RoleType.OWNER)
                        .count());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), test1.getId())
                                        && member.getType() == RoleType.ASSESSOR)
                        .count());
        assertEquals(1,
                memberList.stream()
                        .filter(member ->
                                Objects.equals(member.getUserId(), test2.getId())
                                        && member.getType() == RoleType.DEVELOPER)
                        .count());

    }

    @Test
    void testFindByUserOwnerId() {
        List<Project> list = projectService.findByUserOwnerId(admin.getId());
        assertEquals(1, list.size());
        assertEquals(individualProject.getId(), list.get(0).getId());
    }
}