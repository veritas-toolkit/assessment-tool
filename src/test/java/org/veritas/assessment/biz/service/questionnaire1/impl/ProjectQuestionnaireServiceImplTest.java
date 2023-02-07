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

package org.veritas.assessment.biz.service.questionnaire1.impl;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService1;
import org.veritas.assessment.common.exception.NotFoundException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles("test")
class ProjectQuestionnaireServiceImplTest {
    @Autowired
    private ProjectQuestionnaireService1 service;

    @Autowired
    private ObjectWriter objectWriter;

    @Test
    void testCreate_success() throws IOException {
        assertNotNull(service);
        ProjectQuestionnaire questionnaire = service.create(2, 1);

        log.info("json:\n{}", objectWriter.writeValueAsString(questionnaire));
    }

    @Test
    void testAddMainQuestion_success() {
        int projectId = RandomUtils.nextInt(10, 100);
        ProjectQuestionnaire questionnaire = service.create(projectId, 1);

        ProjectQuestion toAddMain = new ProjectQuestion();
        String content = "content";
        String hint = "hint";
        String part = "B";

        toAddMain.setContent(content);
        toAddMain.setProjectId(projectId);
        toAddMain.setHint(hint);
        toAddMain.setPart(part);
        for (int i = 0; i < 5; i++) {
            ProjectQuestion sub = new ProjectQuestion();
            sub.setPart(part);
            sub.setSubSerial(i + 1);
            sub.setContent(content + "_sub_" + (i + 1));
            sub.setHint(hint + "_hint_" + (i + 1));
            toAddMain.addSub(sub);
        }
        ProjectQuestion afterAdd = service.addMainQuestion(questionnaire.questionnaireId(), toAddMain);
        assertEquals(content, afterAdd.getContent());
        assertEquals(hint, afterAdd.getHint());

        ProjectQuestionnaire newQ = service.findQuestionnaireById(projectId);
        ProjectQuestion newProjectQuestion = newQ.findMainQuestionWitSubByTitle(afterAdd.title());
        assertEquals(content, newProjectQuestion.getContent());
        assertEquals(hint, newProjectQuestion.getHint());
        assertEquals(afterAdd.getSubQuestions().size(), newProjectQuestion.getSubQuestions().size());
        for (int i = 0; i < newProjectQuestion.getSubQuestions().size(); i++) {
            ProjectQuestion afterAddSub = afterAdd.getSubQuestions().get(i);
            ProjectQuestion newSub = newProjectQuestion.getSubQuestions().get(i);

            assertEquals(afterAddSub.getSubSerial(), newSub.getSubSerial());
            assertEquals(afterAddSub.getContent(), newSub.getContent());
            assertEquals(afterAddSub.getHint(), newSub.getHint());
        }

    }

    @Test
    void testDeleteMainQuestion_success() {
        int projectId = 3;
        ProjectQuestionnaire questionnaire = service.create(projectId, 1);
        ProjectQuestion a1 = questionnaire.findMainQuestionWitSubByTitle("A1");

        int result = service.deleteMainQuestion(questionnaire.questionnaireId(), a1.getId());
        assertEquals(a1.getSubQuestions().size() + 1, result);

        ProjectQuestionnaire newQuestionnaire = service.findByProject(projectId);
        assertEquals(questionnaire.getQuestions().size() - 1, newQuestionnaire.getQuestions().size());

        for (int i = 0; i < newQuestionnaire.getQuestions().size(); i++) {
            ProjectQuestion main = questionnaire.getQuestions().get(i + 1);
            ProjectQuestion newMain = newQuestionnaire.getQuestions().get(i);
            assertEquals(main.getContent(), newMain.getContent());
            assertEquals(main.getAnswer(), newMain.getAnswer());
        }

    }

    @Test
    void testDeleteSubQuestion_success() {
        int projectId = 2;
        ProjectQuestionnaire questionnaire = service.create(projectId, 1);
        ProjectQuestion a1 = questionnaire.findMainQuestionWitSubByTitle("A1");
        assertNotNull(a1);
        List<ProjectQuestion> subs = a1.getSubQuestions();
        assertNotNull(subs);
        assertFalse(subs.isEmpty());

        ProjectQuestion firstSub = subs.get(0);
        int result = service.deleteSubQuestion(questionnaire.questionnaireId(), firstSub.getId());
        assertEquals(1, result);

        ProjectQuestionnaire newQuestionnaire = service.findByProject(projectId);
        ProjectQuestion newA1 = newQuestionnaire.findMainQuestionWitSubByTitle("A1");

        assertEquals(a1.getSubQuestions().size() - 1, newA1.getSubQuestions().size());
        for (int i = 0; i < newA1.getSubQuestions().size(); i++) {
            ProjectQuestion sub = a1.getSubQuestions().get(i + 1);
            ProjectQuestion newSub = newA1.getSubQuestions().get(i);
            assertEquals(sub.getContent(), newSub.getContent());
            assertEquals(sub.getAnswer(), newSub.getAnswer());
        }
    }

    @Test
    void testUpdateMainQuestion_success() {
        int projectId = RandomUtils.nextInt(10, 100);
        ProjectQuestionnaire questionnaire = service.create(projectId, 1);

        String title = "E1";
        ProjectQuestion main = questionnaire.findMainQuestionWitSubByTitle(title);

        String content = "new_content";

        main.setContent(content);
        main.getSubQuestions().get(1).setContent(content);

        ProjectQuestion after = service.updateMainQuestionWithSub(projectId, main);
        assertEquals(content, after.getContent());
        assertEquals(content, after.getSubQuestions().get(1).getContent());
    }

    @Test
    void testEditAnswer_success() {
        int projectId = RandomUtils.nextInt(10, 100);
        ProjectQuestionnaire questionnaire = service.create(projectId, 1);

        String title = "E1";
        ProjectQuestion main = questionnaire.findMainQuestionWitSubByTitle(title);

        String answerPrefix = "answer_prefix_";

        main.toList().forEach(q -> q.setAnswer(answerPrefix + RandomStringUtils.randomAlphanumeric(4)));

        service.editAnswer(projectId, main);
    }







}