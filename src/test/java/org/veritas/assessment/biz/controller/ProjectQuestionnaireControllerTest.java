package org.veritas.assessment.biz.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionAnswerInputDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.SimpleQuestionDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.system.config.ControllerExceptionHandler;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProjectQuestionnaireControllerTest {
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectControllerTestUtils projectControllerTestUtils;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testToc_success() throws Exception {
        ProjectDto projectDto = projectControllerTestUtils.createProject();
        MvcResult mvcResult = mockMvc
                .perform(get("/api/project/{projectId}/questionnaire/toc", projectDto.getId())
                        .with(user("1").roles("USER")))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
        QuestionnaireTocDto returnObj = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<QuestionnaireTocDto>() { });
        assertNotNull(returnObj);
        log.info("toc: {}", returnObj);
    }

    @Test
    void testEditAnswer_success() throws Exception {
        ProjectDto projectDto = projectControllerTestUtils.createProject();
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        QuestionNode q = questionnaireVersion.getMainQuestionNodeList().get(0);
        Long questionId = q.getQuestionId();
        QuestionAnswerInputDto dto = new QuestionAnswerInputDto();
        dto.setProjectId(projectDto.getId());
        dto.setQuestionId(questionId);
        dto.setBasedQuestionVid(q.getQuestionVid());
        String answer = "ANSWER";
        dto.setAnswer(answer);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/project/{projectId}/questionnaire/answer", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
        SimpleQuestionDto returnObj = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<SimpleQuestionDto>() {
                });
        log.info("question: {}", returnObj);
        assertNotNull(returnObj);

        QuestionnaireVersion newVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        newVersion.getMainQuestionNodeList().forEach(node -> {
            if (Objects.equals(questionId, node.getQuestionId())) {
                log.info("node: {}", node);
                assertEquals(answer, node.getQuestionVersion().getAnswer());
            }
        });
    }

    @Test
    void testEditAnswer_fail() throws Exception {
        ProjectDto projectDto = projectControllerTestUtils.createProject();
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        QuestionNode q = questionnaireVersion.getMainQuestionNodeList().get(0);
        Long questionId = q.getQuestionId();
        QuestionAnswerInputDto dto = new QuestionAnswerInputDto();
        dto.setProjectId(projectDto.getId());
        dto.setQuestionId(questionId);
        dto.setBasedQuestionVid(q.getQuestionVid());
        String answer = "ANSWER";
        dto.setAnswer(answer);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/project/{projectId}/questionnaire/answer", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
        SimpleQuestionDto returnObj = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<SimpleQuestionDto>() {
                });
        log.info("question: {}", returnObj);
        assertNotNull(returnObj);

        QuestionnaireVersion newVersion = questionnaireService.findLatestQuestionnaire(projectDto.getId());
        newVersion.getMainQuestionNodeList().forEach(node -> {
            if (Objects.equals(questionId, node.getQuestionId())) {
                log.info("node: {}", node);
                assertEquals(answer, node.getQuestionVersion().getAnswer());
            }
        });


        dto.setAnswer("new answer");
        mvcResult = mockMvc.perform(
                        post("/api/project/{projectId}/questionnaire/answer", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
        assertNotNull(mvcResult.getResponse().getContentAsString());
        ControllerExceptionHandler.ErrorMessage errorMessage = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<ControllerExceptionHandler.ErrorMessage>() {
                });
        log.info("question: {}", errorMessage);
        assertNotNull(errorMessage);
        assertEquals("The question's answer has been modified.", errorMessage.getMessage());
    }
}