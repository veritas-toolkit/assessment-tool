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
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionReorderDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateSubQuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateSubQuestionReorderDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        dto.setMainQuestionId(question.getMainQuestionId());
        String content = "new content: " + RandomStringUtils.randomAlphanumeric(20);
        dto.setContent(content);


        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question/{questionId}",
                                template.getId(), question.getId())
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
        log.info("before: -------------");
        logMain(question);
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
        log.info("-----------");
        logMain(newQuestion);
        assertEquals(content, newQuestion.getContent());
    }

    @Test
    void testSubQuestion_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire template = service.create(admin, 1, "test", "test description");
        int count = 0;
        for (TemplateQuestion question : template.getMainQuestionList()) {
            count += 1;
            count += question.getSubList().size();
        }
        log.info("question count: {}", count);

        TemplateQuestion question = template.findMainBySerial("T2");
        logMain(question);
        log.info("question: {}", question);
        TemplateSubQuestionAddDto dto = new TemplateSubQuestionAddDto();
        dto.setTemplateId(template.getId());
        dto.setMainQuestionId(question.getId());
        dto.setSubSerial(4);
        String content = "new content: " + RandomStringUtils.randomAlphanumeric(20);
        dto.setQuestion(content);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question/{questionId}/sub/new",
                                template.getId(), question.getId())
                                .with(user("admin").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TemplateQuestionDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TemplateQuestionDto.class);
        log.info("result: {}", result);

        TemplateQuestionDto subDto = null;
        if (result.getSubQuestionList().size() >= 4) {
            subDto = result.getSubQuestionList().get(4 - 1);
        } else {
            subDto = result.getSubQuestionList().get(result.getSubQuestionList().size() - 1);
        }
        assertEquals(content, subDto.getQuestion());
        TemplateQuestionnaire newQuestionnaire = service.findByTemplateId(template.getId());
        int count2 = 0;
        for (TemplateQuestion main : newQuestionnaire.getMainQuestionList()) {
            count2 += 1;
            count2 += main.getSubList().size();
        }
        log.info("question count: {}", count2);
        TemplateQuestion newQuestion = newQuestionnaire.findMainBySerial("T2");
        log.info("---------------------");
        logMain(newQuestion);
        assertEquals(content, newQuestion.getSubList().get(2).getContent());
    }

    @Test
    void testReorderMainQuestion_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire questionnaire = service.create(admin, 1, "test", "test description");
        List<TemplateQuestion> oldList = questionnaire.findMainQuestionListByPrincipleStep(Principle.EA, AssessmentStep.STEP_0);
        oldList.forEach(this::logMain);
        List<TemplateQuestion> newList = new ArrayList<>(oldList);
        Collections.reverse(newList);
        List<Integer> newIdList = newList.stream().map(TemplateQuestion::getId).collect(Collectors.toList());
        TemplateQuestionReorderDto dto = new TemplateQuestionReorderDto();
        dto.setTemplateId(questionnaire.getId());
        dto.setPrinciple(Principle.EA);
        dto.setStep(AssessmentStep.STEP_0);
        dto.setQuestionIdList(newIdList);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question/reorder",
                                questionnaire.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TemplateQuestionnaireTocDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TemplateQuestionnaireTocDto.class);
        log.info("result: {}", result);
        String resultString = mvcResult.getResponse().getContentAsString();
        int preIndex = Integer.MAX_VALUE;
        for (TemplateQuestion question : oldList) {
            int index = resultString.indexOf(question.getContent());
            assertTrue(index < preIndex);
            preIndex = index;
        }
    }

    @Test
    void testReorderSubQuestion_success() throws Exception {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire questionnaire = service.create(admin, 1, "test", "test description");
        TemplateQuestion main = questionnaire.findMainBySerial("EA5");
        assertNotNull(main);
        log.info("---------------------");
        logMain(main);

        List<TemplateQuestion> subList = main.getSubList();
        List<Integer> oldSubIdList = subList.stream().map(TemplateQuestion::getId).collect(Collectors.toList());
        List<Integer> newSubIdList = new ArrayList<>(oldSubIdList);
        Collections.reverse(newSubIdList);
        TemplateSubQuestionReorderDto dto = new TemplateSubQuestionReorderDto();
        dto.setTemplateId(questionnaire.getId());
        dto.setMainQuestionId(main.getId());
        dto.setNewOrderList(newSubIdList);

        MvcResult mvcResult = mockMvc.perform(
                        post("/api/admin/questionnaire/{templateId}/question/{questionId}/sub/reorder",
                                questionnaire.getId(), main.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        TemplateQuestionDto result =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), TemplateQuestionDto.class);
        log.info("result: {}", result);
        String resultString = mvcResult.getResponse().getContentAsString();
        int index = 0;
        for (TemplateQuestionDto subDto : result.getSubQuestionList()) {
            assertEquals(subDto.getId(), newSubIdList.get(index));
            ++index;
        }
    }

    private void logMain(TemplateQuestion question) {
        log.info("{} {} {} -  : {}", question.getId(), question.getStep(), question.serial(), question.getContent());
        for (TemplateQuestion sub : question.getSubList()) {
            log.info("{} {} {} - {}: {}", sub.getId(), sub.getStep(), sub.serial(), sub.getSubSerial(), sub.getContent());
        }
    }
}