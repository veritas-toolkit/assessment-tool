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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire1.TemplateQuestionnaireService;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void testList_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/admin/questionnaire2")
                        .param("keyword", "te")
                        .param("businessScenario", "1")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testGetQuestionnaireToc_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/admin/questionnaire2/1/toc")
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
        TemplateQuestionnaire template = service.create(1, "test", "test description");

        MvcResult mvcResult = mockMvc.perform(delete("/api/admin/questionnaire/" + template.getTemplateId())
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }
}