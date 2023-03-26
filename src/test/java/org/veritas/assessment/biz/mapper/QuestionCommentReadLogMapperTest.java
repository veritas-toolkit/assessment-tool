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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles("test")
class QuestionCommentReadLogMapperTest {

    @Autowired
    private QuestionCommentReadLogMapper mapper;

    @Test
    void test() {
        assertNotNull(mapper);
    }

    @Test
    @SqlGroup(
            value = {
                    @Sql(statements = "INSERT INTO vat2_question_comment_read_log (user_id, project_id, question_id, latest_read_comment_id) VALUES (1, 2, 3, 4)"),
                    @Sql(statements = "INSERT INTO vat2_question_comment_read_log (user_id, project_id, question_id, latest_read_comment_id) VALUES (1, 2, 13, 5)"),
                    @Sql(statements = "INSERT INTO vat2_question_comment_read_log (user_id, project_id, question_id, latest_read_comment_id) VALUES (1, 2, 23, 6)"),
                    @Sql(statements = "INSERT INTO vat2_question_comment_read_log (user_id, project_id, question_id, latest_read_comment_id) VALUES (1, 2, 43, 7)")
            }
    )
    void testFind() {
        int userId = 1;
        int projectId = 2;
        Map<Long, QuestionCommentReadLog> map = mapper.findLog(userId, projectId);
        assertEquals(4, map.size());
        QuestionCommentReadLog readLog = map.get(3L);
        assertNotNull(readLog);
        assertEquals(4, readLog.getLatestReadCommentId());
        Map<Long, QuestionCommentReadLog> map2 = mapper.findLog(userId, projectId);
        assertEquals(map.hashCode(), map2.hashCode());
    }

    @Test
    void testInsertOrUpdate() {
        QuestionCommentReadLog log1 = new QuestionCommentReadLog(1, 2, 3L, 4);
        int result = mapper.addOrUpdate(log1);
        assertEquals(1, result);

        QuestionCommentReadLog log2 = new QuestionCommentReadLog(1, 2, 3L, 5);
        result = mapper.addOrUpdate(log2);
        assertEquals(1, result);
    }
}