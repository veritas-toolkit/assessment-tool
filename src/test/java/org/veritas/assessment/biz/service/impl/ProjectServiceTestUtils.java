package org.veritas.assessment.biz.service.impl;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.mapper.UserMapper;

import java.util.Date;
import java.util.List;
import java.util.Objects;

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
        return create(BusinessScenarioEnum.PUW);
    }

    public Project create(BusinessScenarioEnum businessScenarioEnum) {
        Objects.requireNonNull(businessScenarioEnum);
        User admin = userMapper.findByUsername("admin");
        assertNotNull(admin);

        Project project = new Project();
        Date now = new Date();
        String formate = "MMdd_HHmmss_SSS_";
        String time = DateFormatUtils.format(now, formate);
        String name = String.format("test_%s_%s_%s",
                businessScenarioEnum.getName(), time, RandomStringUtils.randomAlphanumeric(5));
        project.setName(name);
        project.setDescription("test_description");
        project.setBusinessScenario(businessScenarioEnum.getCode());
        project.setCreatorUserId(admin.getId());
        project.setUserOwnerId(admin.getId());

        project.setPrincipleGeneric(true);
        project.setPrincipleFairness(true);
        project.setPrincipleEA(true);
        project.setPrincipleTransparency(true);

        List<TemplateQuestionnaire> templateQuestionnaireList =
                templateQuestionnaireService.findByKeywordAndBiz(null, businessScenarioEnum.getCode());
        if (templateQuestionnaireList.isEmpty()) {
            throw new IllegalStateException("There is no template questionnaire of " + businessScenarioEnum);
        }
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireList.get(0);
        templateQuestionnaire = templateQuestionnaireService.findByTemplateId(templateQuestionnaire.getId());


        return projectService.createProject(admin, project, templateQuestionnaire);
    }
}
