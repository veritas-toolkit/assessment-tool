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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.mapper.QuestionCommentMapper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class QuestionCommentMapperTest {
    @Autowired
    private QuestionCommentMapper mapper;

    public static List<QuestionComment> data(int count, int userId, long questionId, int projectId) {
        final long MAIN_QUESTION_ID = 22L;
        List<QuestionComment> list = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            QuestionComment comment = new QuestionComment();
            comment.setQuestionId(questionId);
            comment.setMainQuestionId(MAIN_QUESTION_ID);
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
        QuestionComment comment = data(1, 2, 3, 4).get(0);
        int result = mapper.add(comment);
        assertEquals(1, result);
    }

    @Test
    void testFindByQuestionId_success() {
        int count = 10;
        long questionId = 3L;
        List<QuestionComment> commentList = data(count, 2, questionId, 4);
        for (QuestionComment comment : commentList) {
            mapper.add(comment);
        }
        List<QuestionComment> dbList1 = mapper.findByQuestionId(questionId);
        assertEquals(10, dbList1.size());
        log.info("list 1 hashcode: {}", dbList1.hashCode());
        log.info("list 1 size: {}", dbList1.size());
        log.info("list:\n{}", dbList1);

//        dbList1.remove(dbList1.get(0));
//        mapper.add(commentList.get(0));

        List<QuestionComment> dbList2 = mapper.findByQuestionId(questionId);
        log.info("list 1 hashcode: {}", dbList1.hashCode());
        log.info("list 1 size: {}", dbList1.size());

        log.info("list 2 hashcode: {}", dbList2.hashCode());
        log.info("list 2 size: {}", dbList2.size());
    }

    @Test
    void testFindByProjectId() {
        int count = 10;
        long questionId = 20L;
        int projectId = 30;
        int userId = 40;
        for (int i = 0; i < 3; ++i) {
            List<QuestionComment> list = data(count, userId, questionId, projectId + i);
            for (QuestionComment comment : list) {
                mapper.add(comment);
            }
        }
        List<QuestionComment> list = mapper.findByProjectId(projectId);
        assertEquals(count, list.size());
        for (QuestionComment comment : list) {
            assertEquals(projectId, comment.getProjectId());
        }
        log.info("list:\n{}", list);
    }
}