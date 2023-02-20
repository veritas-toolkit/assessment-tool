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

package org.veritas.assessment.biz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocWithMainQuestionDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProjectQuestionnaireEditControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectControllerTestUtils projectControllerTestUtils;

    @Test
    void testAddMainQuestion_success() throws Exception {
        ProjectDto projectDto = projectControllerTestUtils.createProject();
        QuestionAddDto dto = new QuestionAddDto();
        dto.setProjectId(projectDto.getId());
        dto.setPrinciple(Principle.F);
        dto.setStep(AssessmentStep.STEP_1);
        dto.setQuestion("add question" + RandomStringUtils.randomAlphanumeric(20));

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/project/{projectId}/questionnaire/edit/question/new", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        QuestionnaireTocWithMainQuestionDto returnObj =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        new TypeReference<QuestionnaireTocWithMainQuestionDto>() {
                        });
        log.info("question: {}", returnObj);
        assertNotNull(returnObj);
        assertTrue(StringUtils.contains(returnObj.toString(), dto.getQuestion()));
    }

    @Test
    void testDeleteMainQuestion_success() throws Exception {
        ProjectDto projectDto = projectControllerTestUtils.createProject();
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        Long questionId = questionnaireVersion.getMainQuestionNodeList().get(0).getQuestionId();
        MvcResult mvcResult = mockMvc.perform(
                delete("/api/project/{projectId}/questionnaire/edit/question/{questionId}",
                        projectDto.getId(), questionId)
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
        QuestionnaireTocDto returnObj =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        new TypeReference<QuestionnaireTocDto>() {
                        });
        log.info("question: {}", returnObj);
        assertNotNull(returnObj);

        QuestionnaireVersion newVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        newVersion.getMainQuestionNodeList().forEach(node -> {
            if (Objects.equals(questionId, node.getQuestionId())) {
                fail();
            }
        });
    }


}