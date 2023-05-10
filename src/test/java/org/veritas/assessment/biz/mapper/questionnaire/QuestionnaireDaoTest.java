package org.veritas.assessment.biz.mapper.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@Sql("/sql/unit_test_questionnaire.sql")
class QuestionnaireDaoTest {
    @Autowired
    private QuestionnaireDao dao;

    @Test
    void name() {
        assertNotNull(dao);
    }

    @Test
    void testFindLatestQuestionnaire_success() {
        QuestionnaireVersion questionnaireVersion = dao.findLatestQuestionnaire(1);
        log.info("questionnaire: {}", questionnaireVersion);
        assertNotNull(questionnaireVersion);
    }
}