package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.parameters.P;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.controller.ProjectControllerTestUtils;
import org.veritas.assessment.biz.dto.ProjectCreateDto;
import org.veritas.assessment.biz.dto.ProjectDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModelTestUtils;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ModelInsightServiceImplTest {

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

    public Project create() throws RuntimeException {
        ProjectCreateDto createDto = new ProjectCreateDto();
        createDto.setName("new project");
        createDto.setUserOwnerId(1);
        createDto.setDescription("Description");
        createDto.setBusinessScenario(BusinessScenarioEnum.CUSTOMER_MARKETING.getCode());
        createDto.setQuestionnaireTemplateId(4);
        createDto.setPrincipleGeneric(true);
        createDto.setPrincipleFairness(true);
        createDto.setPrincipleEA(true);
        createDto.setPrincipleTransparency(true);
        try {
            Project created = projectServiceTestUtils.create();
            log.info("project: {}", created);
            return created;
        } catch (Exception exception) {
            throw new RuntimeException("Create project failed.", exception);
        }

    }

    @Autowired
    private ModelInsightService modelInsightService;
    @Autowired
    private ModelArtifactService modelArtifactService;

    @Test
    void name() throws Exception {
        User admin = userMapper.findById(1);
        Project project = create();
        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(project.getId());
        assertNotNull(modelInsightService);

        String json = JsonModelTestUtils.loadJson(JsonModelTestUtils.EXAMPLE_CM);

        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, "cm.json");
        // save file out of transaction.
        modelArtifactService.saveJsonFile(modelArtifact);

        modelArtifact.setUploadUserId(admin.getId());
        modelArtifact.setUploadTime(new Date());

        projectService.updateModelArtifact(admin, project.getId(), modelArtifact);
    }
}