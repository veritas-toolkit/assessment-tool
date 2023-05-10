package org.veritas.assessment.biz.entity.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.veritas.assessment.biz.constant.Principle;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class TemplateQuestionTest {

    @Test
    void testDefaultFreeMarkTemplate_mainSuccess() {
        TemplateQuestion templateQuestion = new TemplateQuestion();
        templateQuestion.setPrinciple(Principle.F);
        templateQuestion.setSerialOfPrinciple(2);
        templateQuestion.setSubSerial(0);
        String defaultName = templateQuestion.defaultFreeMarkTemplate();
        log.info("ftl: {}", defaultName);
        assertEquals("F/F2.ftl", defaultName);
    }

    @Test
    void testDefaultFreeMarkTemplate_subSuccess() {
        TemplateQuestion templateQuestion = new TemplateQuestion();
        templateQuestion.setPrinciple(Principle.F);
        templateQuestion.setSerialOfPrinciple(2);
        templateQuestion.setSubSerial(2);

        String defaultName = templateQuestion.defaultFreeMarkTemplate();
        log.info("ftl: {}", defaultName);
        assertEquals("F/F2_S2.ftl", defaultName);
    }
}