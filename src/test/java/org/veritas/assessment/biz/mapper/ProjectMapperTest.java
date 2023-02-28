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

package org.veritas.assessment.biz.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.action.QueryProjectPageableAction;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ProjectMapperTest {
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private void init() {
        for (int i = 0; i < 10; i++) {
            Project project = new Project();
            project.setUserOwnerId(2);
            project.setName("name_user_own_" + i);
            project.setDescription("description_" + i);
            project.setCreatorUserId(2);
            project.setBusinessScenario(BusinessScenarioEnum.PUW.getCode());
            project.setLastEditedTime(new Date());
            projectMapper.addProject(project);
        }
        for (int i = 0; i < 20; i++) {
            Project project = new Project();
            project.setGroupOwnerId(1);
            project.setName("name_group_own_" + i);
            project.setDescription("description_" + i);
            project.setCreatorUserId(2);
            project.setBusinessScenario(BusinessScenarioEnum.PUW.getCode());
            project.setLastEditedTime(new Date());
            projectMapper.addProject(project);
        }
    }

    @Test
    void teatAdd() {
        Project project = new Project();
        int i = 2;
        project.setGroupOwnerId(i + 1);
        project.setName("name_" + i);
        project.setDescription("description_" + i);
        project.setCreatorUserId(2);
        project.setBusinessScenario(4);
        project.setLastEditedTime(new Date());
        project.setPrincipleFairness(true);

        int result = projectMapper.addProject(project);
        assertEquals(1, result);
//        projectMapper.addProject(project);
        log.info("table: {}", jdbcTemplate.queryForList("select * from vat2_project"));
    }

    @Test
    void testFindProjectPageable_byQueryActionSuccess() {
        this.init();
        QueryProjectPageableAction action = new QueryProjectPageableAction();
        action.setKeyWordsString("use group");
        List<Integer> projectIdList = Arrays.asList(1, 2);
        List<Integer> grojectIdList = Arrays.asList(3, 1);


        Pageable<Project> pageable = projectMapper.findProjectPageable(projectIdList, grojectIdList, action);
        log.info("pageable: {}", pageable);
        log.info("records: {}", pageable.getRecords());
    }

    @Test
    void testFindProjectPageable_success() {
        this.init();
        List<Integer> list = Collections.singletonList(1);
        Pageable<Project> pageable = projectMapper.findProjectPageable(null, list, null, "ame", 1, 20);
        assertNotEquals(0, pageable.getTotal());
        log.info("pageable: {}", pageable);
        log.info("records: {}", pageable.getRecords());
    }

    @Test
    void testFindProjectPageable_byDescriptionSuccess() {
        this.init();
        String keyword = "tion_2";
        List<Integer> list = Collections.singletonList(1);
        Pageable<Project> pageable = projectMapper.findProjectPageable(null, list, null, keyword, 1, 20);
        assertNotEquals(0, pageable.getTotal());
        log.info("pageable: {}", pageable);
        log.info("records: {}", pageable.getRecords());
    }

    @Test
    void testFindProjectListByKeyWord_success() {
        assertNotNull(projectMapper);
        this.init();
//        List<Integer> projectIds = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> projectIds = null;
        List<Integer> groupIds = Arrays.asList(1, 2, 3, 4, 5);
        String keyWord = "me_2";
        List<Project> list = projectMapper.findProjectList(projectIds, groupIds, null, keyWord);
        log.info("list size: {}", list.size());
        log.info("list: {}", list);
        assertTrue(list.size() > 0);
        list.forEach(p -> assertTrue(StringUtils.contains(p.getName(), keyWord)));
    }

    @Test
    void testFindProjectListByKeyWord_byDescriptionSuccess() {
        assertNotNull(projectMapper);
        this.init();
//        List<Integer> projectIds = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> projectIds = null;
        List<Integer> groupIds = Arrays.asList(1, 2, 3, 4, 5);
//        String keyWord = "tion_10"; // description_1xxx
        String keyWord = "tion"; // description_1xxx
        List<Project> list = projectMapper.findProjectList(projectIds, groupIds, null, keyWord);
        log.info("list size: {}", list.size());
        log.info("list: {}", list);
        assertTrue(list.size() > 0);
        list.forEach(p -> assertTrue(StringUtils.contains(p.getDescription(), keyWord)));
    }

    @Test
    void testFindProjectListByPrefix_success() {
        assertNotNull(projectMapper);
        this.init();
//        List<Integer> projectIds = Arrays.asList(1, 2, 3, 4, 5);
        List<Integer> projectIds = null;
        List<Integer> groupIds = Arrays.asList(1, 2, 3, 4, 5);
        String prefix = "name_1";
        List<Project> list = projectMapper.findProjectList(projectIds, groupIds, prefix, null);
        log.info("list size: {}", list.size());
        log.info("list: {}", list);
        list.forEach(p -> assertTrue(StringUtils.startsWith(p.getName(), prefix)));
    }

    @Test
    void testDelete_success() throws Exception {
        this.init();
        int projectId = 2;
        Project project = projectMapper.findById(projectId);
        assertNotNull(project);
        int result = projectMapper.delete(project);
        assertEquals(1, result);
        Project project1 = projectMapper.findById(projectId);
        assertNull(project1);
    }
}