package org.veritas.assessment.biz.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Component
public class ProjectServiceTestUtils {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;


    public Project create() {
        User admin = userMapper.findByUsername("admin");
        assertNotNull(admin);

        Project project = new Project();
        project.setName("test");
        project.setDescription("test_description");
//        project.setBusinessScenario(BusinessScenarioEnum.CUSTOMER_MARKETING.getCode());
        project.setBusinessScenario(BusinessScenarioEnum.PUW.getCode());
        project.setCreatorUserId(admin.getId());
        project.setUserOwnerId(admin.getId());
        project.setPrincipleGeneric(true);
        project.setPrincipleFairness(true);
        project.setPrincipleEA(true);
        project.setPrincipleTransparency(true);

        return projectService.createProject(admin, project,
                templateQuestionnaireService.findByTemplateId(5));
    }
}
