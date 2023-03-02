package org.veritas.assessment.biz.service.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.TestUtils;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionAddDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @Test
    void testDeleteMain_success() {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire questionnaire = service.create(admin, 1, "test_template", "template for test");

        TemplateQuestion toDelete = questionnaire.findMainBySerial("G5");
        log.info("G5:\n{}", toDelete);
        TemplateQuestionnaire after = service.deleteMainQuestion(admin, questionnaire.getId(), toDelete.getId());
        TemplateQuestion afterG5 = after.findMainBySerial("G5");
        log.info("G5:\n{}", afterG5);
        assertTrue(StringUtils.startsWith(afterG5.getContent(), "Are there scope boundaries of the proposed"));
        assertNotNull(after.findMainBySerial("G12"));
        assertNull(after.findMainBySerial("G13"));
    }

    @Test
    void testDeleteSub_success() {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire questionnaire = service.create(admin, 1, "test_template", "template for test");

        String serial = "G5";
        TemplateQuestion main = questionnaire.findMainBySerial(serial);
        log.info("G5: {}", main.getContent());
        for (TemplateQuestion s : main.getSubList()) {
            log.info("{},sub[{}], {}", s.serial(), s.getSubSerial(), s.getContent());
        }
        TemplateQuestion toDelete = main.getSubList().get(0);
        TemplateQuestion newMain = service.deleteSubQuestion(admin, questionnaire.getId(), main.getId(), toDelete.getId());
        log.info("G5: {}", newMain.getContent());
        for (TemplateQuestion s : newMain.getSubList()) {
            log.info("{},sub[{}], {}", s.serial(), s.getSubSerial(), s.getContent());
        }
    }

    @Test
    void testAddMainQuestion_failed() {
        User admin = userService.findUserById(1);
        int templateId = 1;
        List<String> list = new ArrayList<>();
        list.add("main");
        for (int i = 1; i < 4; ++i) {
            list.add("Sub " + i);
        }
        assertThrows(IllegalRequestException.class, () -> {
            TemplateQuestionnaire questionnaire = service.addMainQuestion(admin, templateId, Principle.F,
                    AssessmentStep.STEP_2, 6, list);
        });
    }

    @Test
    void testAddMainQuestion_success() {
        User admin = userService.findUserById(1);
        TemplateQuestionnaire questionnaire = service.create(admin, 1, "test_template", "template for test");
        int templateId = questionnaire.getId();
        for (TemplateQuestion question : questionnaire.findMainQuestionListByPrinciple(Principle.F)) {
            log.info("{} {} -  : {}", question.getStep(), question.serial(), question.getContent());
            for (TemplateQuestion sub : question.getSubList()) {
                log.info("{} {} - {}: {}", sub.getStep(), sub.serial(), sub.getSubSerial(), sub.getContent());
            }
        }
        log.info("--------------------------");
        List<String> list = new ArrayList<>();
        list.add("main");
        for (int i = 1; i < 4; ++i) {
            list.add("Sub " + i);
        }
        TemplateQuestionnaire after = service.addMainQuestion(admin, templateId, Principle.F,
                AssessmentStep.STEP_2, 66, list);
        for (TemplateQuestion question : after.findMainQuestionListByPrinciple(Principle.F)) {
            log.info("{} {} -  : {}", question.getStep(), question.serial(), question.getContent());
            for (TemplateQuestion sub : question.getSubList()) {
            log.info("{} {} - {}: {}", sub.getStep(), sub.serial(), sub.getSubSerial(), sub.getContent());
            }
        }
    }
}