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

package org.veritas.assessment.biz.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
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
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionEditDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AdminQuestionnaireControllerTest {

    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TemplateQuestionnaireService service;
    @Autowired
    private UserService userService;

    @Test
    void testList_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/admin/questionnaire")
                        .param("keyword", "te")
                        .param("businessScenario", "1")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetQuestionnaireToc_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/admin/questionnaire/1/toc")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testDelete_fail() throws Exception {
        // delete failed
        MvcResult mvcResult = mockMvc.perform(delete("/api/admin/questionnaire/1")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void testDelete_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire template = service.create(admin, 1, "test", "test description");

        MvcResult mvcResult = mockMvc.perform(delete("/api/admin/questionnaire/" + template.getId())
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        assertNull(service.findByTemplateId(template.getId()));
    }

    @Test
    void testUpdateQuestion_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire template = service.create(admin, 1, "test", "test description");
        TemplateQuestion question = template.findMainBySerial("G1");
        log.info("question: {}", question);
        TemplateQuestionEditDto dto = new TemplateQuestionEditDto();
        dto.setId(question.getId());
        String content = "new content: " + RandomStringUtils.randomAlphanumeric(20);
        dto.setContent(content);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question", template.getId())
                                .with(user("admin").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TemplateQuestionnaire newQuestionnaire = service.findByTemplateId(template.getId());
        TemplateQuestion newQuestion = newQuestionnaire.findMainBySerial("G1");
        assertEquals(content, newQuestion.getContent());
    }

    @Test
    void testMainQuestion_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire template = service.create(admin, 1, "test", "test description");
        TemplateQuestion question = template.findMainBySerial("G1");
        log.info("question: {}", question);
        TemplateQuestionAddDto dto = new TemplateQuestionAddDto();
        dto.setTemplateId(template.getId());
        dto.setPrinciple(Principle.F);
        dto.setStep(AssessmentStep.STEP_2);
        dto.setSerialOfPrinciple(4);
        String content = "new content: " + RandomStringUtils.randomAlphanumeric(20);
        dto.setQuestion(content);
        List<String> list = new ArrayList<>();
        for (int i = 1; i < 4; ++i) {
            list.add("Sub " + i);
        }
        dto.setSubQuestionList(list);


        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question/new", template.getId())
                                .with(user("admin").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TemplateQuestionnaire newQuestionnaire = service.findByTemplateId(template.getId());
        TemplateQuestion newQuestion = newQuestionnaire.findMainBySerial("F4");
        assertEquals(content, newQuestion.getContent());
    }
}