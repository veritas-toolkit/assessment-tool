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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.UserRegisterDto;
import org.veritas.assessment.system.entity.User;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
//@WebMvcTest(UserController.class)
class UserControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Test
    void testGetById_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/user/1")
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testGetById_notFoundFail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/user/99999")
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testGetByPrefix_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/user")
                        .param("prefix", "ad")
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testGetByPrefix_findAllsuccess() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/user")
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testGetByPrefix_notFoundFail() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/user")
                        .param("prefix", "not_found")
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @Test
    void testRegister_success() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("username");
        dto.setFullName("FullName");
        dto.setEmail("username@example.com");
        dto.setPassword("djakf@T$H#$ty39");
        MvcResult mvcResult = mockMvc.perform(put("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        User user = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), User.class);
        assertFalse(user.isShouldChangePassword());

    }

    @Test
    void testRegister_badRequestFail() throws Exception {
        UserRegisterDto dto = new UserRegisterDto();
        dto.setUsername("username");
        dto.setFullName("FullName");
        dto.setEmail("fake_email");
        dto.setPassword(null);
        mockMvc.perform(put("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }
}