package org.veritas.assessment.system.auth;

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

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Sql(scripts = "/sql/unit_test_user.sql")
class CustomAuthenticationFilterTest {
    @Autowired
    private MockMvc mockMvc;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testLogin_success() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", "admin");
        map.put("password", "123456");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testLogin_testSuccess() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", "test_1");
        map.put("password", "123456");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testLogin_fail() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", "admin");
        map.put("password", "fake");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
    }

    @Test
    void testLogin_userNotFoundFail() throws Exception {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("username", "admin22222");
        map.put("password", "fake");

        MvcResult mvcResult = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(map)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn();
    }
}