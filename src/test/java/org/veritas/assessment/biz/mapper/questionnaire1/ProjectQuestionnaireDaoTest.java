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

package org.veritas.assessment.biz.mapper.questionnaire1;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ProjectQuestionnaireDaoTest {
    @Autowired
    private ProjectQuestionnaireDao dao;

    @Autowired
    private ProjectQuestionnaireMapper mapper;

    @Autowired
    private ProjectQuestionMapper projectQuestionMapper;

    @Test
    void testAdd_success() throws IOException {
        log.info("dao hashcode: {}", dao.hashCode());
        log.info("mapper hashcode: {}", mapper.hashCode());
        log.info("projectQuestionMapper hashcode: {}", projectQuestionMapper.hashCode());

        int projectId = 5;
        ProjectQuestionnaire questionnaire = new ProjectQuestionnaire();
        questionnaire.setProjectId(projectId);
        questionnaire.setPartATitle("Part A");
        questionnaire.setPartBTitle("Part B");
        questionnaire.setPartCTitle("Part C");
        questionnaire.setPartDTitle("Part D");
        questionnaire.setPartETitle("Part E");

        String[] partArray = {"A", "B", "C", "D", "E"};
        List<ProjectQuestion> questionList = new ArrayList<>();
        for (String part : partArray) {
            for (int partSerial = 1; partSerial < 6; partSerial++) {
                for (int subSerial = 0; subSerial < 5; subSerial++) {
                    ProjectQuestion question = new ProjectQuestion();
                    question.setProjectId(projectId);
                    question.setPart(part);
                    question.setPartSerial(partSerial);
                    question.setSubSerial(subSerial);
                    question.setContent(String.format("Project[%d] [%s%d] [%d], content", projectId, part, partSerial, subSerial));
                    question.setHint(String.format("Project[%d] [%s%d] [%d], hint.", projectId, part, partSerial, subSerial));
                    question.setEditTime(new Date());
                    question.setEditable(true);
                    questionList.add(question);
                }
            }
        }
        questionnaire.addQuestions(questionList);
        ProjectQuestionnaire result = dao.add(questionnaire);

        assertNotNull(result);
        log.info("result:\n{}", result);
//        log.info("json:\n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(questionnaire));
        ProjectQuestionnaire questionnaire1 = dao.findQuestionnaire(5);
        log.info("json:\n{}", new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(questionnaire1));
    }
}