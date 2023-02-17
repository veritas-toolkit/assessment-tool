/*
 * Copyright 2021 MAS Veritas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.veritas.assessment.biz.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;
import org.veritas.assessment.biz.entity.BusinessScenario;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.model.SpecialParameterContainer;
import org.veritas.assessment.biz.service.GraphService;
import org.veritas.assessment.biz.service.ImageService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.SystemService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.Objects;

@Service
@Slf4j
public class ModelInsightServiceImpl implements ModelInsightService {

    @Autowired
    private Configuration configuration;

    @Autowired
    private GraphService graphService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private VeritasProperties veritasProperties;
    @Autowired
    private SystemService systemService;
    @Autowired
    private ImageService imageService;

    @Override
    @Transactional
    public void autoGenerateAnswer(Project project, ModelArtifact modelArtifact) {
        ModelMap modelMap = genModelMap(project, modelArtifact);

        QuestionnaireVersion questionnaire = questionnaireService.findLatestQuestionnaire(project.getId());

        // for each question node.
        // check: need to auto generate answer.
        // generate the answer

        // update the answer to the questionnaire. -- create new version.
    }

    private ModelMap genModelMap(Project project, ModelArtifact modelArtifact) {
        JsonModel jsonModel = modelArtifact.getJsonModel();
        ModelMap modelMap = new ModelMap();
        modelMap.addAttribute("project", project);
        modelMap.addAttribute("jsonModel", jsonModel);
        modelMap.addAttribute("fairness", jsonModel.getFairness());
        modelMap.addAttribute("transparency", jsonModel.getTransparency());

        SpecialParameterContainer specialParameterContainer = new SpecialParameterContainer(jsonModel);
        modelMap.addAttribute("specialParameter", specialParameterContainer);

        GraphContainer container = graphService.createAllGraph(modelArtifact);
        modelMap.addAttribute("graphContainer", container);

        BusinessScenario businessScenario = systemService.findBusinessScenarioByCode(project.getBusinessScenario());
        if (businessScenario == null) {
            throw new ErrorParamException("The business scenario of the project is not legal.");
        }
        modelMap.addAttribute("businessScenario", businessScenario);
        return modelMap;
    }


    // generate the answer
    private boolean answerContent(ModelMap modelMap, Integer businessScenario, ProjectQuestion question) {
        Objects.requireNonNull(modelMap);
        Objects.requireNonNull(question);

        String ftlName = templateName(question);
        try (StringWriter writer = new StringWriter()) {
            Template template = template(businessScenario, question);
            template.process(modelMap, writer);
            String answer = writer.toString();
            if (StringUtils.isBlank(answer)) {
                answer = null;
            }
            question.setAnswer(answer);
            return true;
        } catch (TemplateNotFoundException templateNotFoundException) {
            log.warn("Template not found for name: {}", ftlName);
        } catch (Exception exception) {
            if (question.isMainQuestion()) {
                log.warn("Create answer content fail: question[{}]", question, exception);
            } else {
                log.warn("Create answer content fail: question[{}], sub[{}]",
                        question, question.getSubSerial(), exception);
            }
            if (veritasProperties.isAutoGenerateAnswerStrict()) {
                if (question.isMainQuestion()) {
                    throw new IllegalStateException(
                            String.format("create question[%s] failed.", question.title()),
                            exception);
                } else {
                    throw new IllegalStateException(
                            String.format("create question[%s_S%d] failed.", question.title(), question.getSubSerial()),
                            exception);
                }
            }
        }
        return false;
    }

    private Template template(Integer businessScenario, ProjectQuestion question) throws IOException {
        Objects.requireNonNull(businessScenario);
        Objects.requireNonNull(question);

        final String DEFAULT_FORMATTER = "answer/default/%s%d/%s%d.ftl";
        final String DEFAULT_SUB_FORMATTER = "answer/default/%s%d/%s%d_S%d.ftl";

        final String S_FORMATTER = "answer/S_%02d/%s%d/%s%d.ftl";
        final String S_SUB_FORMATTER = "answer/S_%02d/%s%d/%s%d_S%d.ftl";

        String specialTemplateName = null;
        if (question.isMainQuestion()) {
            specialTemplateName = String.format(S_FORMATTER, businessScenario, question.getPart(),
                    question.getPartSerial(), question.getPart(), question.getPartSerial());
        } else {
            specialTemplateName = String.format(S_SUB_FORMATTER, businessScenario, question.getPart(),
                    question.getPartSerial(), question.getPart(), question.getPartSerial(), question.getSubSerial());
        }
        Template template = null;
        try {
            configuration.setEncoding(Locale.ENGLISH, "UTF-8");
            template = configuration.getTemplate(specialTemplateName);
        } catch (TemplateNotFoundException ignored) {
            // Yes do nothing!
        }
        if (template != null) {
            log.debug("Using template: {}", specialTemplateName);
            return template;
        } else {
            String defaultTemplateName = null;
            if (question.isMainQuestion()) {
                defaultTemplateName = String.format(DEFAULT_FORMATTER, question.getPart(), question.getPartSerial(),
                        question.getPart(), question.getPartSerial());
            } else {
                defaultTemplateName = String.format(DEFAULT_SUB_FORMATTER, question.getPart(), question.getPartSerial(),
                        question.getPart(), question.getPartSerial(), question.getSubSerial());
            }
            log.debug("Using template: {}", defaultTemplateName);
            return configuration.getTemplate(defaultTemplateName);
        }
    }

    private String templateName(ProjectQuestion question) {
        final String FORMATTER = "answer/%s%d/%s%d.ftl";
        final String SUB_FORMATTER = "answer/%s%d/%s%d_S%d.ftl";
        if (question.isMainQuestion()) {
            return String.format(FORMATTER, question.getPart(), question.getPartSerial(),
                    question.getPart(), question.getPartSerial());
        } else {
            return String.format(SUB_FORMATTER, question.getPart(), question.getPartSerial(),
                    question.getPart(), question.getPartSerial(), question.getSubSerial());
        }
    }

}
