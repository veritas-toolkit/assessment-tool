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

package org.veritas.assessment.biz.entity.questionnaire1;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
class ProjectQuestionTest {

    public static List<ProjectQuestion> data() {
        return data(1);
    }

    public static List<ProjectQuestion> data(Integer projectId) {
        String[] partArray = {"A", "B", "C", "D", "E"};
        final int questionsCountOfPart = 5;
        final int subCountOfQuestion = 4;
//        int id = RandomUtils.nextInt();
        int id = 1000;
        List<ProjectQuestion> list = new ArrayList<>();
        for (String part : partArray) {
            for (int partSeq = 1; partSeq < questionsCountOfPart; ++partSeq) {
                for (int subSeq = 0; subSeq < subCountOfQuestion; ++subSeq) {
                    ProjectQuestion question = new ProjectQuestion();
                    question.setId(id);
                    ++id;
                    question.setProjectId(projectId);
                    question.setPart(part);
                    question.setPartSerial(partSeq);
                    question.setSubSerial(subSeq);
                    question.setContent("Question Content: " + RandomStringUtils.randomAlphanumeric(10));
                    question.setHint("Hint: " + RandomStringUtils.randomAlphanumeric(6));
                    question.setEditTime(new Date());
                    question.setAnswer("Answer: " + RandomStringUtils.randomAlphanumeric(6));
                    question.setAnswerEditTime(new Date());
                    list.add(question);
                }
            }
        }
        return list;
    }


    @Test
    void test() {
        List<ProjectQuestion> list = data();
        Collections.shuffle(list);

        list.forEach(e -> {
            log.info("before: {}, {}", e.title(), e.getSubSerial());
        });
        log.info("==============================================");
        log.info("==============================================");

        List<ProjectQuestion> mainWithSubList = ProjectQuestion.toTree2(list);
        mainWithSubList.forEach(e -> {
            log.info("main: {}, {}", e.title(), e.getSubSerial());
            e.getSubQuestions().forEach(sub -> {
                log.info("--> sub: {}, {}", sub.title(), sub.getSubSerial());
            });
        });

    }

    @Test
    void testNextSubSerial() {
        List<ProjectQuestion> list = data();
        List<ProjectQuestion> mainWithSubList = ProjectQuestion.toTree2(list);
        ProjectQuestion projectQuestion = mainWithSubList.get(0);
        int nextSub = projectQuestion.nextSubSerial();
        log.info("next sub: {}", nextSub);
        assertEquals(4, nextSub);
    }
}