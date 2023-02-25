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
import org.apache.commons.lang3.RandomStringUtils;
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

@Transactional
@Slf4j
@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProjectControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    @Lazy
    private ProjectService projectService;
    @Autowired
    @Lazy
    private UserService userService;
    @Autowired
    @Lazy
    private CacheManager cacheManager;

    @BeforeEach
    public void evictAllCaches() {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Autowired
    TemplateQuestionnaireService templateQuestionnaireService;

    @Test
    void findByKeyword_success() throws Exception {
        String keyword = "keyword";

        User admin = userService.findUserByUsernameOrEmail("admin");
        Project project = new Project();
        project.setName("asdlfka_" + keyword + "_djfadifadf");
        project.setDescription("description");
        project.setUserOwnerId(admin.getId());
        project.setBusinessScenario(1);
        project.setPrincipleGeneric(true);
        project.setPrincipleFairness(true);

        projectService.createProject(admin, project, templateQuestionnaireService.findByTemplateId(1));

        MvcResult mvcResult = mockMvc.perform(get("/api/project")
                        .param("keyword", keyword)
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Pageable<ProjectDto> pageable = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), new TypeReference<Pageable<ProjectDto>>() {
                });
        assertEquals(1, pageable.getPage());
        assertEquals(1, pageable.getPageCount());
        assertEquals(1, pageable.getTotal());
        assertEquals(project.getName(), pageable.getRecords().get(0).getName());
    }

    @Test
    void testCreate_success() throws Exception {
        ProjectCreateDto dto = new ProjectCreateDto();
        dto.setName("new_project" + RandomStringUtils.randomAlphanumeric(5));
        dto.setUserOwnerId(1);
        dto.setDescription("Description");
        dto.setBusinessScenario(1);
        dto.setQuestionnaireTemplateId(1);
        dto.setPrincipleGeneric(true);
        dto.setPrincipleFairness(true);
        dto.setPrincipleEA(true);
        dto.setPrincipleTransparency(true);

        MvcResult mvcResult = mockMvc.perform(post("/api/project/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectDto projectDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(projectDto);
        assertNotNull(projectDto.getId());
        assertEquals(dto.getName(), projectDto.getName());
        assertEquals(dto.getDescription(), projectDto.getDescription());
        assertEquals(dto.getUserOwnerId(), projectDto.getUserOwner().getId());
        assertEquals(dto.getBusinessScenario(), projectDto.getBusinessScenario());
        assertEquals(1, projectDto.getCreatorUserId());
    }

    @Test
    void testModify_success() throws Exception {
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

        createDto.setDescription("Description_new");


        ProjectBasicDto basicDto = new ProjectBasicDto();
        basicDto.setId(projectDto.getId());
        basicDto.setName("xxxxxxxxxx");
        basicDto.setDescription("description_");
        basicDto.setPrincipleGeneric(true);
        basicDto.setPrincipleFairness(true);
        basicDto.setPrincipleEA(true);
        basicDto.setPrincipleTransparency(true);

        mvcResult = mockMvc.perform(post("/api/project/{projectId}", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(basicDto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();

        projectDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);
        assertNotNull(projectDto);
        assertNotNull(projectDto.getId());
        assertEquals(basicDto.getId(), projectDto.getId());
        assertEquals(basicDto.getName(), projectDto.getName());
        assertEquals(basicDto.getDescription(), projectDto.getDescription());
    }

    @Test
    void testListMember_success() throws Exception {
        ProjectDto projectDto = createProject();
        MvcResult mvcResult = mockMvc
                .perform(get("/api/project/{projectId}/member", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        List<Member> list = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Member>>() {
                });
        assertEquals(1, list.size());
        assertEquals(projectDto.getCreatorUserId(), list.get(0).getUserId());
    }

    @Test
    void testDetail_success() throws Exception {
        ProjectDto projectDto = createProject();
        MvcResult mvcResult = mockMvc
                .perform(get("/api/project/{projectId}/detail", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER")))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectDetailDto detailDto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ProjectDetailDto>() {
                });
        log.info("detail:\n{}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(detailDto));
        assertNotNull(detailDto);
    }

    @Test
    @Sql("/sql/unit_test_user.sql")
    void testAddMember_success() throws Exception {
        ProjectDto projectDto = createProject();
        List<MembershipDto> dtoList = new ArrayList<>();
        for (int id = 2; id <= 3; id++) {
            MembershipDto membershipDto = new MembershipDto();
            membershipDto.setUserId(id);
            membershipDto.setType(RoleType.DEVELOPER);
            dtoList.add(membershipDto);
        }
        MvcResult mvcResult = mockMvc
                .perform(put("/api/project/{projectId}/member", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoList)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        List<Member> list = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                new TypeReference<List<Member>>() {
                });
        log.info("member list:\n{}",
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(list));
        assertEquals(dtoList.size(), list.size());
    }

    private ProjectDto createProject() throws Exception {
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

    @Test
    void testJson() throws Exception {
        ProjectDto projectDto = createProject();
        MockMultipartFile jsonFile = new MockMultipartFile(
                "file", "xxx.json", "application/json",
                JsonModelTestUtils.loadJson(JsonModelTestUtils.EXAMPLE_CM).getBytes(StandardCharsets.UTF_8));
        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.multipart(
                                        "/api/project/{projectId}/modelArtifact", projectDto.getId())
                                .file(jsonFile)
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ModelArtifactDto modelArtifactDto =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ModelArtifactDto.class);
        assertEquals(DigestUtils.sha256Hex(jsonFile.getBytes()), modelArtifactDto.getJsonContentSha256());
        assertEquals(jsonFile.getOriginalFilename(), modelArtifactDto.getFilename());

        // get info
        MvcResult mvcResult1 = mockMvc.perform(
                        get("/api/project/{projectId}/modelArtifact", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ModelArtifactDto modelArtifactDto2 =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ModelArtifactDto.class);
        assertEquals(modelArtifactDto.getFilename(), modelArtifactDto2.getFilename());
        assertEquals(modelArtifactDto.getUploadTime(), modelArtifactDto2.getUploadTime());
        assertEquals(modelArtifactDto.getJsonContentSha256(), modelArtifactDto2.getJsonContentSha256());
        assertEquals(modelArtifactDto.getProjectId(), modelArtifactDto2.getProjectId());

        // download
        MvcResult mvcResult3 = mockMvc.perform(
                        get("/api/project/{projectId}/modelArtifact/download", projectDto.getId())
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        assertEquals(modelArtifactDto2.getJsonContentSha256(),
                DigestUtils.sha256Hex(mvcResult3.getResponse().getContentAsByteArray()));
    }

    @Test
    void testImage() throws Exception {
        ProjectDto projectDto = createProject();

        byte[] imageContent;
        try (InputStream inputStream = new ClassPathResource("").getInputStream()) {
            imageContent = IOUtils.toByteArray(inputStream);
        }

        MockMultipartFile imageFile = new MockMultipartFile(
                "image", "xxx.png", "", imageContent);
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.multipart(
                                "/api/project/{projectId}/image", projectDto.getId())
                        .file(imageFile)
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        String url = mvcResult.getResponse().getContentAsString();

        MvcResult mvcResult2 = mockMvc.perform(get("/" + url)
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        assertEquals(imageContent.length, mvcResult2.getResponse().getContentAsByteArray().length);


    }
}