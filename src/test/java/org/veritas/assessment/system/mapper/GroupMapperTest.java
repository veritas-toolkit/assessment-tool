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

package org.veritas.assessment.system.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.Group;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class GroupMapperTest {
    @Autowired
    private GroupMapper groupMapper;


    @Test
    void testAddGroup_success() {
        Group group = new Group();
        group.setCreatorUserId(1);
        group.setName("test-group");
        group.setDescription("Group for test.");
        Date now = new Date();
        group.setCreatedTime(now);
        group.setLastModifiedTime(now);
        log.info("before: {}", group);
        int result = groupMapper.addGroup(group);
        assertEquals(1, result);
        log.info("after: {}", group);
    }

    @Test
    void testFindById_success() {
        List<Group> groupList = addList(22);
        for (Group group : groupList) {
//            log.info("group: {}", group);
            Group g = groupMapper.findById(group.getId());
            assertEquals(group.getId(), g.getId());
            assertEquals(group.getName(), g.getName());
            assertEquals(group.getDescription(), g.getDescription());
            log.info("group: {}", groupMapper.findById(group.getId()));
        }
    }

    private List<Group> addList(int number) {
        List<Group> groupList = new ArrayList<>(number);
        int creatorUid = 1;
        for (int j = 1; j <= number; j++) {
            Group group = new Group();
            group.setCreatorUserId(creatorUid);
            group.setName("test-group-" + j);
            group.setDescription("Group for test. NO_" + j);
            Date now = new Date();
            group.setCreatedTime(now);
            group.setLastModifiedTime(now);
            groupMapper.addGroup(group);
            groupList.add(group);
        }
        return groupList;
    }

    @Test
    void testFindGroupListPageable_success() {
        addList(10);
        Pageable<Group> pageable = groupMapper.findGroupListPageable(1, 20, "test");
        assertNotNull(pageable);
        assertNotEquals(0, pageable.getTotal());
        log.info("pageable: {}", pageable);
    }


    @Test
    void testDelete_success() {
        addList(10);
        int groupId = 2;
        Group group = groupMapper.findById(groupId);
        assertNotNull(group);
        int result = groupMapper.delete(group);
        assertEquals(1, result);
        Group group1 = groupMapper.findById(groupId);
        assertNull(group1);

        Group newGroup = new Group();
        newGroup.setName(group.getName());
        newGroup.setDescription(group.getDescription());
        newGroup.setCreatorUserId(group.getCreatorUserId());
        newGroup.setCreatedTime(new Date());
        newGroup.setLastModifiedTime(new Date());
        groupMapper.addGroup(newGroup);
        assertNotNull(newGroup.getId());
        assertNotEquals(newGroup.getId(), group.getId());

    }
}