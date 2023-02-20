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
import org.springframework.boot.test.autoconfigure.core.AutoConfigureCache;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.BusinessScenarioDto;
import org.veritas.assessment.biz.dto.v1.questionnaire.TemplateQuestionnaireBasicDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureCache
class SystemControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetBizScenarioList_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/system/business_scenario")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<BusinessScenarioDto> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<BusinessScenarioDto>>() {
                });
        assertNotNull(list);
        assertNotEquals(0, list.size());
        log.info("list of biz scenario: {}", list);
    }


    @Test
    void testGetTemplateQuestionnaireList_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/system/questionnaire_template")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TemplateQuestionnaireBasicDto> list = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<TemplateQuestionnaireBasicDto>>() {
                });
        assertNotNull(list);
        assertNotEquals(0, list.size());
        log.info("list of TemplateQuestionnaire: {}", list);
    }


    @Test
    void testRegisterSupported_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/system/config/register-supported")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("false", mvcResult.getResponse().getContentAsString());
    }


    @Test
    void testModifyAccount_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/system/config/modify-account-supported")
                        .with(user("admin").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        assertEquals("false", mvcResult.getResponse().getContentAsString());
    }
}