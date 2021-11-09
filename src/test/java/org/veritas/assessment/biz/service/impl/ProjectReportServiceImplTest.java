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

package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.JsonModel;
import org.veritas.assessment.biz.entity.JsonModelTest;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.ProjectQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class ProjectReportServiceImplTest {

    @Autowired
    ProjectReportService reportService;
    @Autowired
    UserService userService;
    @Autowired
    ProjectService projectService;
    @Autowired
    private ProjectQuestionnaireService questionnaireService;
    @Autowired
    private ModelArtifactService modelArtifactService;
    @Autowired
    private ModelInsightService modelInsightService;

    private void fillAnswer(Project project) {
        ProjectQuestionnaire questionnaire = questionnaireService.findQuestionnaireById(project.getId());
        for (ProjectQuestion main : questionnaire.getQuestions()) {
            if (main.completed()) {
                continue;
            }
            for (ProjectQuestion projectQuestion : main.toList()) {
                projectQuestion.setAnswer("answer_" + RandomStringUtils.randomAlphanumeric(40));
                projectQuestion.setAnswerEditTime(new Date());
            }
            questionnaireService.editAnswer(project.getId(), main);
        }
        log.info("All answers filled.");
    }

    @Test
    void testPreviewReportPdf_success() throws Exception {
        User user = userService.findUserById(1);

        int projectId = 2;
        Project project = new Project();
        project.setId(projectId);
        project.setName("Project name");
        project.setDescription("Project Description");
        project.setUserOwnerId(user.getId());
        project.setBusinessScenario(1);

        projectService.createProject(user, project, 1);
        fillAnswer(project);

        byte[] content = reportService.previewReportPdf(project);
        assertNotNull(content);

        String now = DateFormatUtils.format(new Date(), "yyyy_MM_dd_HH_mm_ss");
        String filename = "target/out_" + now + ".pdf";
//        String filename = "target/out.pdf";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
            IOUtils.write(content, fileOutputStream);
        }
    }

    @Test
    void testPreview_success() throws Exception {
        User user = userService.findUserById(1);
        int projectId = 2;
        Project project = new Project();
        project.setId(projectId);
        project.setName("Project name");
        project.setDescription("Project Description");
        project.setUserOwnerId(user.getId());
        project.setBusinessScenario(1);
        projectService.createProject(user, project, 1);
        fillAnswer(project);

        String html = reportService.previewReport(project);
        assertNotNull(html);
        String filename = "target/out.html";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
            IOUtils.write(html, fileOutputStream, StandardCharsets.UTF_8);
        }

    }

    @Test
    void testPreview_pdfSuccess() throws Exception {
        User admin = userService.findUserById(1);
        Project project = new Project();
        project.setUserOwnerId(admin.getId());
        project.setName("test-" + RandomStringUtils.randomAlphanumeric(5));

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(RandomStringUtils.randomAlphanumeric(10));
            builder.append(" ");
        }
        project.setDescription("description: " + builder);
        project.setBusinessScenario(1);

        project = projectService.createProject(admin, project, 1);


        JsonModel jsonModel = JsonModelTest.load(JsonModelTest.creditScoringUrl);
        String jsonUrl = JsonModelTest.creditScoringUrl;
        String jsonFilename = FilenameUtils.getName(jsonUrl);
        String json = JsonModelTest.loadJson(jsonUrl);

        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, jsonFilename);
        modelArtifactService.saveJsonFile(modelArtifact);

        modelInsightService.autoGenerateAnswer(project, modelArtifact);

        ProjectQuestionnaire questionnaire = questionnaireService.findByProject(project.getId());


        byte[] content = reportService.previewReportPdf(project);
        assertNotNull(content);
        String now = DateFormatUtils.format(new Date(), "yyyy_MM_dd_HH_mm_ss");
        String filename = "target/" + now + "_out.pdf";
        try (FileOutputStream fileOutputStream = new FileOutputStream(filename)) {
            IOUtils.write(content, fileOutputStream);
        }
    }

    @Test
    void testCreateReport_success() throws Exception {
        User admin = userService.findUserById(1);
        Project project = new Project();
        project.setUserOwnerId(admin.getId());
//        project.setName("test-" + RandomStringUtils.randomAlphanumeric(5));
        project.setName("Test for Export");

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            builder.append(RandomStringUtils.randomAlphanumeric(10));
            builder.append(" ");
        }
        project.setDescription("description: " + builder);
        project.setBusinessScenario(1);

        project = projectService.createProject(admin, project, 1);

//        ModelArtifact modelArtifact = new ModelArtifact();
//        modelArtifact.setProjectId(project.getId());

//        String filename = JsonModelTest.creditScoringUrl;
//        modelArtifact.setFilename(FilenameUtils.getName(filename));
//
//        modelArtifact.setJsonContent(IOUtils.toString(new ClassPathResource(filename).getURL(),
//                StandardCharsets.UTF_8));

        String jsonUrl = JsonModelTest.creditScoringUrl;
        String filename = FilenameUtils.getName(jsonUrl);
        String json = JsonModelTest.loadJson(jsonUrl);

        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, filename);

        modelArtifact.setUploadUserId(admin.getId());
        modelArtifact.setUploadTime(new Date());

        modelArtifactService.saveJsonFile(modelArtifact);

        projectService.updateModelArtifact(admin, project.getId(), modelArtifact);

        ProjectReport report = reportService.createReport(admin, project, "0.0.1", "This is the first version");
        assertNotNull(report);
        log.info("report:\n{}", report);

    }
}