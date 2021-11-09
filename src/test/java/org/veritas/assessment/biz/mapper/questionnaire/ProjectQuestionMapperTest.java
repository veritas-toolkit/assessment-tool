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
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestion;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ProjectQuestionMapperTest {
    @Autowired
    ProjectQuestionMapper mapper;

    List<ProjectQuestion> data() {
        List<ProjectQuestion> list = new ArrayList<>();
        Date now = new Date();
        for (int i = 0; i < 5; i++) {
            ProjectQuestion question = new ProjectQuestion();
            question.setProjectId(1);
            question.setPart("A");
            question.setPartSerial(2);
            question.setSubSerial(i);
            question.setEditable(true);
            question.setEditTime(now);
            question.setContent("Content " + RandomStringUtils.randomAlphanumeric(50));
            question.setHint("Hint: " + RandomStringUtils.randomAlphanumeric(50));
            list.add(question);
        }

        return list;
    }

    @Test
    void name() {
        assertNotNull(mapper);
        List<ProjectQuestion> list = data();
        int result = mapper.addQuestionList(list);
        log.info("result: {}", result);
        log.info("list: {}", list);

    }

    @Test
    void testUpdateQuestionSerial_success() {
        List<ProjectQuestion> list = data();
        int addResult = mapper.addQuestionList(list);
        int result = mapper.updateQuestionSerial(list);
        assertEquals(addResult, result);
        assertEquals(list.size(), result);
    }

    @Test
    void testEditAnswer_success() {
        List<ProjectQuestion> list = data();
        int addResult = mapper.addQuestionList(list);
        list.get(0).setAnswer("first: edit the answer.");
        list.get(list.size() - 1).setAnswer("edit the answer.");
        int result = mapper.updateAnswerList(list);
        assertEquals(list.size(), result);
    }
}