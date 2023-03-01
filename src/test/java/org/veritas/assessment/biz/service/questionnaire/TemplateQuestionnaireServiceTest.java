package org.veritas.assessment.biz.service.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.TestUtils;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class TemplateQuestionnaireServiceTest {
    @Autowired
    TemplateQuestionnaireService service;

    @Autowired
    UserService userService;

    @Test
    void name() {
        assertNotNull(service);
    }

    @Test
    void findAllTemplateBasic() {
        List<TemplateQuestionnaire> list = service.findAllTemplateBasic();
        assertEquals(BusinessScenarioEnum.values().length, list.size());
    }

    @Test
    void testCreate_success() {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire newOne = service.create(admin, 1, "test_template", "template for test");

        log.info("create: \n{}", newOne);
        log.info("create: \n{}", TestUtils.toJson(newOne));
        assertNotNull(newOne);
    }

    @Test
    void testDelete_failed() {
        Assertions.assertThrows(IllegalRequestException.class, () -> {
            service.delete(1);
        });
    }

    @Test
    void testDelete_success() {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire newOne = service.create(admin, 1, "test_template", "template for test");

        service.delete(newOne.getId());
        TemplateQuestionnaire findAgain = service.findByTemplateId(newOne.getId());
        assertNull(findAgain);

    }


}