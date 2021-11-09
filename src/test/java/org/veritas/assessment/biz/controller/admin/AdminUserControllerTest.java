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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.UserCreateDto;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class AdminUserControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    public void testListUser_success() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get("/api/admin/user")
                        .with(user("admin").roles("ADMIN", "USER"))
                        .param("keyword", "test")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is2xxSuccessful()).andReturn();
        Pageable<User> pageable = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<Pageable<User>>() {
                });

        assertEquals(2, pageable.getRecords().size());
    }

    @Test
    public void testCreateUserList_success() throws Exception {
        List<UserCreateDto> dtoList = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            UserCreateDto dto = new UserCreateDto();
            dto.setUsername("username_" + i);
            dto.setFullName("Full Name");
            dto.setEmail("email@example.com");
            dto.setPassword("ab#$%#$T$B124");
            dto.setProjectLimited(2);
            dto.setGroupLimited(3);
            dtoList.add(dto);
        }
        MvcResult mvcResult = mockMvc.perform(put("/api/admin/user/new")
                        .with(user("admin").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andExpect(status().isCreated()).andReturn();
    }

    @Test
    public void testCreateUserList_fail1() throws Exception {
        List<UserCreateDto> dtoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UserCreateDto dto = new UserCreateDto();
            dto.setUsername("username");
            dto.setFullName("FullName");
            dto.setEmail("email@example.com");
            dto.setPassword("abcdefg1");
            dtoList.add(dto);
        }
        MvcResult mvcResult = mockMvc.perform(put("/api/admin/user/new")
                        .with(user("admin").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    public void testCreateUserList_fail2() throws Exception {
        List<UserCreateDto> dtoList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            UserCreateDto dto = new UserCreateDto();
            dto.setUsername("username");
            dto.setFullName("FullName");
            dto.setEmail("abc@ee.com");
            dto.setPassword("jasdifasdlk");
            dto.setGroupLimited(10);
            dto.setProjectLimited(20);
            dtoList.add(dto);
        }
        MvcResult mvcResult = mockMvc.perform(put("/api/admin/user/new")
                        .with(user("admin").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print())
                .andExpect(status().isBadRequest()).andReturn();
    }
}