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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.QuestionDto;
import org.veritas.assessment.biz.dto.QuestionnaireDto;
import org.veritas.assessment.biz.entity.JsonModelTest;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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
    private MockMvc mockMvc;


    @Test
    void testQuestionnaire() throws Exception {
        ProjectDto projectDto = createProject("for test");
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire/edit", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());

        QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire> questionnaireDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire>>() {
                });
        assertNotNull(questionnaireDto);

        ProjectQuestion question = questionnaireDto.getPartList().get(0).getQuestionList().get(0);

        // edit question content
        QuestionDto dto = new QuestionDto();
        dto.setId(question.getId());
        dto.setPart(question.getPart());
        dto.setPartSerial(question.getPartSerial());
        dto.setContent("content");
        List<QuestionDto> subList = new ArrayList<>();
        for (ProjectQuestion subQuestion : question.getSubQuestions()) {
            QuestionDto sub = new QuestionDto();
            sub.setId(question.getId());
            sub.setPart(question.getPart());
            sub.setPartSerial(question.getPartSerial());
            sub.setContent("content");
            subList.add(sub);
        }
        dto.setSubQuestions(subList);

        MvcResult mvcResult2 = mockMvc.perform(
                        post("/api/project/{projectId}/questionnaire/edit/question",
                                projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testAddMainQuestion_success() throws Exception {
        ProjectDto projectDto = createProject("for main question");
        QuestionDto dto = new QuestionDto();
        dto.setPart("B");
        dto.setContent("content");
        MvcResult mvcResult = mockMvc.perform(
                        put("/api/project/{projectId}/questionnaire/edit/question", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectQuestion projectQuestion = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<ProjectQuestion>() {
                });
        log.info("question: {}", projectQuestion);
        assertNotNull(projectQuestion);
    }

    @Test
    void testDeleteMainQuestion_success() throws Exception {
        ProjectDto projectDto = createProject("for delete main question.");
        QuestionDto dto = new QuestionDto();
        dto.setId(1);
        dto.setContent("content");
        MvcResult mvcResult = mockMvc.perform(
                        delete("/api/project/{projectId}/questionnaire/edit/question", projectDto.getId())
                                .param("questionId", "1")
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
//        QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire> projectQuestion =
//                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
//                        new TypeReference<QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire>>() {
//                        });
//        log.info("question: {}", projectQuestion);
//        assertNotNull(projectQuestion);
        assertNotNull(mvcResult.getResponse().getContentAsString());
    }


    private ProjectDto createProject(String name) throws Exception {
        ProjectCreateDto createDto = new ProjectCreateDto();
        createDto.setName(name);
        createDto.setUserOwnerId(1);
        createDto.setDescription("Description");
        createDto.setBusinessScenario(1);
        createDto.setQuestionnaireTemplateId(1);

        MvcResult mvcResult = mockMvc.perform(put("/api/project/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectDto projectDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(projectDto);
        assertNotNull(projectDto.getId());
        assertEquals(createDto.getDescription(), projectDto.getDescription());

        MockMultipartFile jsonFile = new MockMultipartFile(
                "file", "xxx.json", "application/json",
                JsonModelTest.loadJson(JsonModelTest.creditScoringUrl).getBytes(StandardCharsets.UTF_8));
        MvcResult mvcResult2 = mockMvc.perform(
                        MockMvcRequestBuilders.multipart(
                                        "/api/project/{projectId}/modelArtifact", projectDto.getId())
                                .file(jsonFile)
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();

        return projectDto;
    }
}