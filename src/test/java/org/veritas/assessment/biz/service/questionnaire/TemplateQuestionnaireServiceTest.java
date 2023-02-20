package org.veritas.assessment.biz.service.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaireJson;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class TemplateQuestionnaireServiceTest {
    @Autowired
    TemplateQuestionnaireService service;


    @Test
    void name() {
        assertNotNull(service);
    }

    @Test
    void testLoad() {
        TemplateQuestionnaireJson templateQuestionnaireJson = service.load();
        log.info("object:\n{}", templateQuestionnaireJson);

    }
}