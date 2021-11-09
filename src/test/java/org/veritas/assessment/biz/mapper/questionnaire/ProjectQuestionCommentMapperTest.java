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

package org.veritas.assessment.biz.mapper.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestionComment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProjectQuestionCommentMapperTest {
    @Autowired
    private ProjectQuestionCommentMapper mapper;

    public static List<ProjectQuestionComment> data(int count, int userId, int questionId, int projectId) {
        List<ProjectQuestionComment> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ProjectQuestionComment comment = new ProjectQuestionComment();
            comment.setQuestionId(questionId);
            comment.setProjectId(projectId);
            comment.setUserId(userId);
            comment.setCreatedTime(new Date());
            comment.setComment("Comment--" + RandomStringUtils.randomAlphanumeric(10));
            list.add(comment);
        }


        return list;
    }

    @Test
    void test() {
        assertNotNull(mapper);
    }

    @Test
    void testAdd_success() {
        ProjectQuestionComment comment = data(1, 2, 3, 4).get(0);
        int result = mapper.add(comment);
        assertEquals(1, result);
    }

    @Test
    void testFindByQuestionId_success() {
        int count = 10;
        int questionId = 3;
        List<ProjectQuestionComment> commentList = data(count, 2, questionId, 4);
        for (ProjectQuestionComment comment : commentList) {
            mapper.add(comment);
        }
        List<ProjectQuestionComment> dbList1 = mapper.findByQuestionId(questionId);
        assertEquals(10, dbList1.size());
        log.info("list 1 hashcode: {}", dbList1.hashCode());
        log.info("list 1 size: {}", dbList1.size());
        log.info("list:\n{}", dbList1);

//        dbList1.remove(dbList1.get(0));
//        mapper.add(commentList.get(0));

        List<ProjectQuestionComment> dbList2 = mapper.findByQuestionId(questionId);
        log.info("list 1 hashcode: {}", dbList1.hashCode());
        log.info("list 1 size: {}", dbList1.size());

        log.info("list 2 hashcode: {}", dbList2.hashCode());
        log.info("list 2 size: {}", dbList2.size());
    }

    @Test
    void testFindByProjectId() {
        int count = 10;
        int questionId = 20;
        int projectId = 30;
        int userId = 40;
        for (int i = 0; i < 3; ++i) {
            List<ProjectQuestionComment> list = data(count, userId, questionId, projectId + i);
            for (ProjectQuestionComment comment : list) {
                mapper.add(comment);
            }
        }
        List<ProjectQuestionComment> list = mapper.findByProjectId(projectId);
        assertEquals(count, list.size());
        for (ProjectQuestionComment comment : list) {
            assertEquals(projectId, comment.getProjectId());
        }
        log.info("list:\n{}", list);
    }
}