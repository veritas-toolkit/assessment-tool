package org.veritas.assessment.biz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.dto.ExportReportDto;
import org.veritas.assessment.biz.dto.ModelArtifactDto;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.dto.SuggestionVersionDto;
import org.veritas.assessment.biz.entity.JsonModelTest;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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
class ProjectReportControllerTest {
    ObjectMapper objectMapper = new ObjectMapper();
    User admin;
    ProjectDto projectDto;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() throws Exception {
        this.projectDto = createProject("new project_" + hashCode());
    }

    ProjectDto createProject(String name) throws Exception {
        admin = userService.findUserById(1);

        ProjectCreateDto createDto = new ProjectCreateDto();
        createDto.setName(name);
        createDto.setUserOwnerId(1);
        createDto.setDescription("Description");
        createDto.setBusinessScenario(1);
        createDto.setQuestionnaireTemplateId(1);

        MvcResult mvcResult = mockMvc.perform(put("/api/project/new")
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ProjectDto projectDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), ProjectDto.class);

        MockMultipartFile jsonFile = new MockMultipartFile(
                "file", "xxx.json", "application/json",
                JsonModelTest.loadJson(JsonModelTest.creditScoringUrl).getBytes(StandardCharsets.UTF_8));
        MvcResult mvcResult2 = mockMvc.perform(
                        MockMvcRequestBuilders.multipart(
                                        "/api/project/{projectId}/modelArtifact", projectDto.getId())
                                .file(jsonFile)
                                .with(user("1").roles("ADMIN", "USER"))
                                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        ModelArtifactDto modelArtifactDto = objectMapper.readValue(
                mvcResult2.getResponse().getContentAsString(), ModelArtifactDto.class);
        assertNotNull(modelArtifactDto);

        return projectDto;
    }

    @Test
    void testPreviewHtml() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(
                        "/api/project/{projectId}/report/preview", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
    }


    @Test
    void testPreviewPdf() throws Exception {
        MvcResult mvcResult = mockMvc.perform(get(
                        "/api/project/{projectId}/report/preview_pdf", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
    }

    @Test
    void testSuggestionVersion() throws Exception {
        ProjectDto pdto = createProject("for_suggestion");
        MvcResult mvcResult = mockMvc.perform(get(
                        "/api/project/{projectId}/report/suggestion-version", pdto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        SuggestionVersionDto dto = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(), SuggestionVersionDto.class);
        assertNotNull(dto);
        assertNull(dto.getLatestVersion());
        log.info("suggestion: {}", dto);
    }

    @Test
    void testExportPdf() throws Exception {
        ExportReportDto dto = new ExportReportDto();
        dto.setMessage("To review");
        dto.setVersion("2.3.5");

        MvcResult mvcResult = mockMvc.perform(post(
                        "/api/project/{projectId}/report/export", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(dto)))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        log.info("content size: {}", mvcResult.getResponse().getContentLength());
        for (String header : mvcResult.getResponse().getHeaderNames()) {
            log.info("header:{} : {}", header, mvcResult.getResponse().getHeaderValues(header));
        }

        // test suggestion again
        MvcResult mvcResult2 = mockMvc.perform(get(
                        "/api/project/{projectId}/report/suggestion-version", projectDto.getId())
                        .with(user("1").roles("ADMIN", "USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print()).andExpect(status().is2xxSuccessful())
                .andReturn();
        SuggestionVersionDto dto2 = objectMapper.readValue(
                mvcResult2.getResponse().getContentAsString(), SuggestionVersionDto.class);
        assertNotNull(dto2);
        assertEquals("2.3.5", dto2.getLatestVersion());
        assertEquals(3, dto2.getSuggestionVersionList().size());
        log.info("suggestion: {}", dto2);
    }


}