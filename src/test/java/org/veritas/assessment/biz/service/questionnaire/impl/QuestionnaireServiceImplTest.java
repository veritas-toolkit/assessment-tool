package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.TestUtils;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class QuestionnaireServiceImplTest {
    @Autowired
    QuestionnaireService questionnaireService;

    @Autowired
    TemplateQuestionnaireService templateQuestionnaireService;

    @Test
    void name() {
        assertNotNull(questionnaireService);
        assertNotNull(templateQuestionnaireService);
    }

    @Autowired
    QuestionnaireDao questionnaireDao;

    @Autowired
    ProjectMapper projectMapper;


    private QuestionnaireVersion create(int projectId, int templateId, int userId) {
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireService.findByTemplateId(templateId);
        Date now = new Date();
        Project project = new Project();
        project.setBusinessScenario(templateQuestionnaire.getBusinessScenario().getCode());
        project.setCreatorUserId(userId);
        project.setId(projectId);
        project.setName("project-for-test-" + userId + "-" + projectId);
        project.setUserOwnerId(userId);
        project.setDescription("project for test.");
        project.setCreatedTime(now);
        project.setPrincipleGeneric(true);
        project.setPrincipleFairness(true);
        project.setPrincipleEA(true);
        project.setPrincipleTransparency(true);

        QuestionnaireVersion questionnaire = questionnaireService.createByTemplate(userId, project, now, templateQuestionnaire);
        questionnaireService.saveNewQuestionnaire(questionnaire);

        project.setCurrentQuestionnaireVid(questionnaire.getVid());
        projectMapper.insert(project);
        return questionnaire;

    }

    @Test
    void testCreat() throws Exception {
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireService.findByTemplateId(1);
        int userId = 8;
        int projectId = 10;
        Date now = new Date();
        Project project = new Project();
        project.setBusinessScenario(templateQuestionnaire.getBusinessScenario().getCode());
        project.setCreatorUserId(userId);
        project.setId(projectId);
        project.setCreatedTime(now);
        QuestionnaireVersion questionnaireVersion = questionnaireService.createByTemplate(userId, project, now, templateQuestionnaire);
        log.info("questionnaire: {}", questionnaireVersion);
        log.info("questionnaire:\n{}", TestUtils.toJson(questionnaireVersion));
        questionnaireService.saveNewQuestionnaire(questionnaireVersion);

        QuestionnaireVersion q = questionnaireDao.findLatestQuestionnaire(projectId);
        log.info("select from database: {}", q);
        log.info("select from database: {}", TestUtils.toJson(q));
//        assertEquals(questionnaireVersion.toString(), q.toString());
        assertEquals(TestUtils.toJson(questionnaireVersion), TestUtils.toJson(q));
    }



    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void testEditAnswer_success() {
        int projectId = 2;
        QuestionnaireVersion questionnaireVersion = this.create(projectId, 1, 88);
        QuestionNode f1 = questionnaireVersion.findMainQuestionBySerial("F1");
        assertNotNull(f1);
        QuestionNode sub = f1.getSubList().get(0);
        int editor = 28;
        questionnaireService.editAnswer(projectId, sub.getQuestionId(), sub.getQuestionVid(),
                "new answer", editor);
        String[] tables = {
                "vat2_project",
                "vat2_questionnaire_version",
                "vat2_question_node",
                "vat2_question_meta",
                "vat2_question_version",
        };
        for (String table : tables) {
            log.info("table: [{}]\n{}", table, jdbcTemplate.queryForList("select * from " + table));

        }
//        String table = "vat2_questionnaire_version";
//        log.info("table: [{}]\n {}", table, jdbcTemplate.queryForList("select * from " + table));
//        table = "vat2_question_meta";
//        log.info("table: [{}]\n {}", table, jdbcTemplate.queryForList("select * from " + table));
//        table = "vat2_question_version";
//        log.info("table: [{}]\n {}", table, jdbcTemplate.queryForList("select * from " + table));
    }
}