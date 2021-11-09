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

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.veritas.assessment.biz.dto.UserChangePasswordDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testModifyAccountInfo_fail() throws Exception {
        UserSimpleDto dto = new UserSimpleDto();
        dto.setUsername("abc");
        dto.setFullName("abc");
        dto.setEmail("email");
        MvcResult mvcResult = mockMvc.perform(post("/api/account")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testModifyAccountInfo_success() throws Exception {
        UserSimpleDto dto = new UserSimpleDto();
        dto.setUsername("admin");
        dto.setFullName("admin");
        dto.setEmail("email@example.com");
        MvcResult mvcResult = mockMvc.perform(post("/api/account")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void testChangePassword_failed() throws Exception {
        UserChangePasswordDto dto = new UserChangePasswordDto();
        dto.setId(1);
        dto.setOldPassword("423t4$#T#$BD345");
        dto.setNewPassword("423t4$#T#$BD345");

        MvcResult mvcResult = mockMvc.perform(post("/api/account/change_password")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().isBadRequest())
                .andReturn();
        String message = mvcResult.getResponse().getContentAsString();
        log.error("message: {}", message);
        assertTrue(StringUtils.contains(message, "The new password should not be same as the old."));
    }
}