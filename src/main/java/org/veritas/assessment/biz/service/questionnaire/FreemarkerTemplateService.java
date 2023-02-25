package org.veritas.assessment.biz.service.questionnaire;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.service.SystemService;
import org.veritas.assessment.system.config.VeritasProperties;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
@Service
public class FreemarkerTemplateService {

    @Autowired
    private VeritasProperties veritasProperties;



    @Autowired
    private Configuration configuration;

    @Autowired
    private SystemService systemService;
    final String ANSWER_PREFIX = "answer/";
    @PostConstruct
    public void loadAllTemplates() {
        configuration.setEncoding(Locale.ENGLISH, StandardCharsets.UTF_8.name());
    }

    public Template findTemplate(BusinessScenarioEnum businessScenarioEnum, String templateFilename) {
        if (businessScenarioEnum == null || StringUtils.isEmpty(templateFilename)) {
            return null;
        }
        String fullname = ANSWER_PREFIX + businessScenarioEnum.getAnswerTemplatePath() + "/" + templateFilename;

        Template template = null;
        try {
            template = configuration.getTemplate(fullname);
        } catch (TemplateNotFoundException exception) {
            log.warn("Not found the template.", exception);
        } catch (IOException exception) {
            log.warn("load template failed.", exception);
        }
        return template;
    }

    public Template findTemplate(Integer businessScenarioNo, QuestionNode questionNode) {
        if (businessScenarioNo == null || questionNode == null) {
            return null;
        }
        QuestionMeta meta = questionNode.getMeta();
        if (meta == null) {
            return null;
        }
        BusinessScenarioEnum businessScenario = BusinessScenarioEnum.ofCode(businessScenarioNo);
        Principle principle = questionNode.getPrinciple();
        String templateFilename = meta.getAnswerTemplateFilename();
        if (businessScenario == null || StringUtils.isEmpty(businessScenario.getAnswerTemplatePath())) {
            log.warn("Business Scenario[NO:{}]: {}", businessScenarioNo, businessScenario);
            return null;
        }
        if (principle == null) {
            return null;
        }
        if (StringUtils.isEmpty(templateFilename)) {
            return null;
        }

        String answerTemplate = String.format(ANSWER_PREFIX + "%s/%s/%s",
                businessScenario.getAnswerTemplatePath(), principle.getShortName(), templateFilename);
        Template template = null;
        try {
            template = configuration.getTemplate(answerTemplate);
        } catch (TemplateNotFoundException exception) {
            log.warn("Not found the template.", exception);
        } catch (IOException exception) {
            log.warn("load template failed.", exception);
        }
        return template;
    }

}
