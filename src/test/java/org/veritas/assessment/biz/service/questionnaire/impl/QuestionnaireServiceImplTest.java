package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.TestUtils;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
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

    @Test
    void testCreat() throws Exception {
        TemplateQuestionnaire templateQuestionnaire = templateQuestionnaireService.findByTemplateId(1);
        int userId = 8;
        int projectId = 10;
        Project project = new Project();
        project.setBusinessScenario(templateQuestionnaire.getBusinessScenario());
        project.setCreatorUserId(userId);
        project.setId(projectId);

        Date now = new Date();
        QuestionnaireVersion questionnaireVersion = questionnaireService.createByTemplate(userId, project, now, 1);
        log.info("questionnaire: {}", questionnaireVersion);
        log.info("questionnaire:\n{}", TestUtils.toJson(questionnaireVersion));
        questionnaireService.saveNewQuestionnaire(questionnaireVersion);

        QuestionnaireVersion q = questionnaireDao.findLatestQuestionnaire(projectId);
        log.info("select from database: {}", q);
        log.info("select from database: {}", TestUtils.toJson(q));
//        assertEquals(questionnaireVersion.toString(), q.toString());
        assertEquals(TestUtils.toJson(questionnaireVersion), TestUtils.toJson(q));
    }
}