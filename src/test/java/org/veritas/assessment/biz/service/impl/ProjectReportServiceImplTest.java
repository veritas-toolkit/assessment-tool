package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModelTestUtils;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProjectReportServiceImplTest {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;
    @Autowired
    private QuestionnaireService questionnaireService;
    @Autowired
    private ProjectServiceTestUtils projectServiceTestUtils;
    @Autowired
    private ModelInsightService modelInsightService;
    @Autowired
    private ModelArtifactService modelArtifactService;
    @Autowired
    private ProjectReportService projectReportService;

    public Project create() throws RuntimeException {
        try {
            Project created = projectServiceTestUtils.create();
            log.info("project: {}", created);
            return created;
        } catch (Exception exception) {
            throw new RuntimeException("Create project failed.", exception);
        }
    }
    @Test
    void testCreateReport_puwSuccess() throws Exception {
        User admin = userMapper.findById(1);
        Project project = projectServiceTestUtils.create(BusinessScenarioEnum.PUW);
        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(project.getId());
        assertNotNull(questionnaire);
        assertNotNull(projectReportService);
        String json = JsonModelTestUtils.loadJson(JsonModelTestUtils.EXAMPLE_PUW);
        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, "unit-test.json");
        // save file out of transaction.
        modelArtifactService.saveJsonFile(modelArtifact);

        modelArtifact.setUploadUserId(admin.getId());
        modelArtifact.setUploadTime(new Date());

        projectService.updateModelArtifact(admin, project.getId(), modelArtifact);
        ProjectReport report = projectReportService.createReport(admin, project, "1.0.0", "V1.0.0");
        log.info("report: {}", report);
    }

    @Test
    public void testCreateReport_success() throws Exception {
        testCreateReport_success(BusinessScenarioEnum.BASE_REGRESSION, JsonModelTestUtils.EXAMPLE_BR);
        testCreateReport_success(BusinessScenarioEnum.BASE_CLASSIFICATION, JsonModelTestUtils.EXAMPLE_BC);
        testCreateReport_success(BusinessScenarioEnum.CREDIT_SCORING, JsonModelTestUtils.EXAMPLE_CS);
        testCreateReport_success(BusinessScenarioEnum.CUSTOMER_MARKETING, JsonModelTestUtils.EXAMPLE_CM);
        testCreateReport_success(BusinessScenarioEnum.PUW, JsonModelTestUtils.EXAMPLE_PUW);
    }

    private void testCreateReport_success(BusinessScenarioEnum businessScenarioEnum, String jsonPath) throws Exception {
        Project project = projectServiceTestUtils.create(businessScenarioEnum);
        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(project.getId());
        assertNotNull(questionnaire);
        assertNotNull(projectReportService);
        String json = JsonModelTestUtils.loadJson(jsonPath);

        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, "unit-test.json");
        // save file out of transaction.
        modelArtifactService.saveJsonFile(modelArtifact);
        User admin = userMapper.findById(1);
        modelArtifact.setUploadUserId(admin.getId());
        modelArtifact.setUploadTime(new Date());
        projectService.updateModelArtifact(admin, project.getId(), modelArtifact);
        ProjectReport report = projectReportService.createReport(admin, project, "1.0.0", "V1.0.0");
        log.info("report: {}", report);
    }

}