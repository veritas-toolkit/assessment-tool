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

package org.veritas.assessment.biz.entity.questionnaire;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class ProjectVersionQuestionTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static List<ProjectVersionQuestion> data(int projectId) {
        List<ProjectQuestion> list = ProjectQuestionTest.data(projectId);
        return list.stream().map(ProjectVersionQuestion::new).collect(Collectors.toList());

    }

    @Test
    void test() throws Exception {

        ProjectQuestion origin = new ProjectQuestion();
        origin.setId(100);
        origin.setPart("A");
        origin.setPartSerial(1);
        origin.setSubSerial(0);
        origin.setContent("question");
        origin.setHint("hint");
        origin.setEditTime(new Date());
        origin.setEditable(false);
        origin.setAnswer("answer");
        origin.setAnswerEditTime(new Date());
        origin.setProjectId(999);

        List<ProjectQuestion> subList = new ArrayList<>();
        for (int subSerial = 1; subSerial <= 4; subSerial++) {
            ProjectQuestion sub = new ProjectQuestion();
            origin.setId(origin.getId() + subSerial);
            origin.setPart("A");
            origin.setPartSerial(1);
            origin.setSubSerial(subSerial);
            origin.setContent("question");
            origin.setHint("hint");
            origin.setEditTime(new Date());
            origin.setEditable(false);
            origin.setAnswer("answer");
            origin.setAnswerEditTime(new Date());
            origin.setProjectId(999);
            subList.add(sub);
        }
        origin.setSubQuestions(subList);

//        ProjectVersionQuestion versionQuestion = new ProjectVersionQuestion();
//        versionQuestion.copyFrom(origin);
        ProjectVersionQuestion versionQuestion = new ProjectVersionQuestion(origin);

        log.info("version:\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(versionQuestion));
        assertEquals(origin.title(), versionQuestion.title());
        assertEquals(origin.getAnswer(), versionQuestion.getAnswer());
        assertEquals(origin.getProjectId(), versionQuestion.getProjectId());
    }

    @Test
    void name() throws Exception {
        List<ProjectQuestion> list = ProjectQuestionTest.data(2);
//        List<ProjectVersionQuestion> versionQuestionList = new ArrayList<>(list.size());
        for (ProjectQuestion question : list) {
            ProjectVersionQuestion versionQuestion = new ProjectVersionQuestion(question);
//            versionQuestion.copyFrom(question);
            log.info("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(versionQuestion));
        }

    }
}