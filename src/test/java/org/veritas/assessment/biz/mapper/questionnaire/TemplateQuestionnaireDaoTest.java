package org.veritas.assessment.biz.mapper.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class TemplateQuestionnaireDaoTest {
    @Autowired
    private TemplateQuestionnaireDao dao;

    @Test
    void test() {
        assertNotNull(dao);
    }

    @Test
    void testFindAll_success() {
        List<TemplateQuestionnaire> list = dao.findAll();
        log.info("list: {}", list);
        assertFalse(list.isEmpty());
    }

    @Test
    void testFindTemplatePageable_success() {
        Pageable<TemplateQuestionnaire> p =
                dao.findTemplatePageable("test", "t", 1, 1, 20);
        assertTrue(p.getTotal() > 0);
        log.info("page: {}", p);
    }
}