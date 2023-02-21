package org.veritas.assessment.biz.service.questionnaire;

import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class FreemarkerTemplateServiceTest {

    @Autowired
    FreemarkerTemplateService service;

    @Test
    void testFindTemplate_success() throws IOException {
        QuestionNode questionNode = new QuestionNode();
        QuestionMeta meta = new QuestionMeta();
        meta.setAnswerTemplateFilename("EA1.ftl");
        questionNode.setMeta(meta);
        questionNode.setPrinciple(Principle.EA);
        Template template = service.findTemplate(1, questionNode);
        assertNotNull(template);
        log.debug("template: {}", template);
    }

    @Test
    void testFindTemplate_byEnumSuccess() {

        Template template = service.findTemplate(BusinessScenarioEnum.BASE_CLASSIFICATION, "EA/EA1.ftl");
        assertNotNull(template);
    }
}