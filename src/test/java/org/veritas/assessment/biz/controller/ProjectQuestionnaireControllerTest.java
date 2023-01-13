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
import org.apache.commons.lang3.StringUtils;
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
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.dto.v1.questionnaire.QuestionnaireForProjectDto;
import org.veritas.assessment.biz.entity.JsonModelTest;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
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
class ProjectQuestionnaireControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetQuestionnaireWithAllAnswer() throws Exception {
        ProjectDto projectDto = createProject("for_get_questionnaire");
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire> dto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire>>() {
                }
        );
        assertNotNull(dto);
        long count = dto.getPartList().get(2).getQuestionList().get(0).toList()
                .stream().filter(ProjectQuestion::hasAnswer).count();
        assertTrue(count > 0);
    }

    @Test
    void testGetQuestionnaireWithFirstAnswer() throws Exception {
        ProjectDto projectDto = createProject("for_get_questionnaire_only_with_first_answer");
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .param("onlyWithFirstAnswer", "true")
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire> dto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire>>() {
                }
        );
        assertNotNull(dto);
        for (int i = 1; i < dto.getPartList().size(); i++) {
            long c = dto.getPartList().get(i).getQuestionList().stream()
                    .filter(q -> StringUtils.isNotBlank(q.getAnswer())).count();
            assertEquals(0, c);

        }
    }

    @Test
    void testQuestion() throws Exception {
        ProjectDto projectDto = createProject("for_get_question");
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire> dto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire>>() {
                }
        );
        assertNotNull(dto);
        ProjectQuestion projectQuestion = dto.getPartList().get(2).getQuestionList().get(0);

        // get question
        MvcResult mvcResult2 = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire/question/{questionId}",
                                projectDto.getId(), projectQuestion.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectQuestion q =
                objectMapper.readValue(mvcResult2.getResponse().getContentAsString(), ProjectQuestion.class);
        assertEquals(projectQuestion.getId(), q.getId());
        assertEquals(projectQuestion.getAnswer(), q.getAnswer());
        assertEquals(projectQuestion.getContent(), q.getContent());

        // edit answer
        q.setAnswer("new answer");
        MvcResult mvcResult3 = mockMvc.perform(post("/api/project/{projectId}/questionnaire/answer",
                        projectDto.getId(), projectQuestion.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(q)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectQuestion q2 =
                objectMapper.readValue(mvcResult3.getResponse().getContentAsString(), ProjectQuestion.class);
        assertEquals(projectQuestion.getId(), q2.getId());
        assertEquals(projectQuestion.getContent(), q2.getContent());
        assertNotEquals(projectQuestion.getAnswer(), q2.getAnswer());

    }

    @Test
    void testComment() throws Exception {
        ProjectDto projectDto = createProject("for_get_question");
        MvcResult mvcResult = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire> dto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire>>() {
                }
        );
        assertNotNull(dto);
        ProjectQuestion projectQuestion = dto.getPartList().get(1).getQuestionList().get(0);

        // add comment
        int seq = 1;
        for (QuestionnaireForProjectDto.QuestionnairePart<ProjectQuestion> part : dto.getPartList()) {
            for (ProjectQuestion question : part.getQuestionList()) {
                QuestionCommentCreateDto commentCreateDto = new QuestionCommentCreateDto();
                commentCreateDto.setQuestionId(question.getId());
                commentCreateDto.setComment("comment" + seq);
                ++seq;
                MvcResult mvcResult2 = mockMvc.perform(
                                put("/api/project/{projectId}/questionnaire/comment",
                                        projectDto.getId())
                                        .with(user("1").roles("ADMIN", "USER"))
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                        .andDo(print()).andExpect(status().is2xxSuccessful())
                        .andReturn();
            }


        }

        // find questionnaire's comments
        MvcResult mvcResult3 = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire/comment",
                                projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        Map<Integer, List<QuestionCommentDto>> map = objectMapper.readValue(
                mvcResult3.getResponse().getContentAsString(),
                new TypeReference<Map<Integer, List<QuestionCommentDto>>>() {
                }
        );
        assertNotNull(map);
        assertTrue(map.entrySet().size() > 0);

        // find question's comments
        // find questionnaire's comments
        MvcResult mvcResult4 = mockMvc.perform(
                        get("/api/project/{projectId}/questionnaire/question/{questionId}/comment",
                                projectDto.getId(), projectQuestion.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        Map<Integer, List<QuestionCommentDto>> map2 = objectMapper.readValue(
                mvcResult3.getResponse().getContentAsString(),
                new TypeReference<Map<Integer, List<QuestionCommentDto>>>() {
                }
        );
        log.info("map 2: {}", map2);
        assertNotNull(map2);
        assertTrue(map2.entrySet().size() > 0);


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