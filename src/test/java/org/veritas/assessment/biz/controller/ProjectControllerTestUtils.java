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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.ModelArtifactDto;
import org.veritas.assessment.biz.dto.ProjectBasicDto;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.dto.ProjectDetailDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModelTestUtils;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.dto.MembershipDto;
import org.veritas.assessment.system.entity.Member;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
class ProjectControllerTestUtils {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;



    public ProjectDto createProject() throws Exception {
        ProjectCreateDto createDto = new ProjectCreateDto();
        createDto.setName("new project");
        createDto.setUserOwnerId(1);
        createDto.setDescription("Description");
        createDto.setBusinessScenario(1);
        createDto.setQuestionnaireTemplateId(1);
        createDto.setPrincipleGeneric(true);
        createDto.setPrincipleFairness(true);
        createDto.setPrincipleEA(true);
        createDto.setPrincipleTransparency(true);

        MvcResult mvcResult = mockMvc.perform(post("/api/project/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectDto projectDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(projectDto);
        assertNotNull(projectDto.getId());
        assertEquals(createDto.getDescription(), projectDto.getDescription());
        return projectDto;
    }



}