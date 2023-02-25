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

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import org.veritas.assessment.biz.action.EditAnswerAction;
import org.veritas.assessment.biz.constant.BusinessScenarioEnum;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.model.SpecialParameterContainer;
import org.veritas.assessment.biz.service.GraphService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.questionnaire.FreemarkerTemplateService;
import org.veritas.assessment.common.exception.ErrorParamException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class ModelInsightServiceImpl implements ModelInsightService {

    @Autowired
    private GraphService graphService;


    @Autowired
    private FreemarkerTemplateService freemarkerTemplateService;

    @Override
    public List<EditAnswerAction> insight(Project project,
                                          QuestionnaireVersion questionnaireVersion,
                                          ModelArtifact modelArtifact) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(questionnaireVersion);
        Objects.requireNonNull(modelArtifact);
        ModelMap modelMap = genModelMap(project, modelArtifact);
        List<QuestionNode> allQuestionList = questionnaireVersion.finAllQuestionNodeList();
        List<EditAnswerAction> actionList = new ArrayList<>();
        for (QuestionNode questionNode : allQuestionList) {
            Template template = freemarkerTemplateService.findTemplate(project.getBusinessScenario(), questionNode);
            if (template != null) {
                // write the freemarker output to a StringWriter
                StringWriter stringWriter = new StringWriter();
                try {
                    template.process(modelMap, stringWriter);
                    String answer = StringUtils.trimToNull(stringWriter.toString());
                    EditAnswerAction action = new EditAnswerAction();
                    action.setProjectId(project.getId());
                    action.setQuestionId(questionNode.getQuestionId());
                    action.setBasedQuestionVid(questionNode.getQuestionVid());
                    action.setAnswer(answer);
                    actionList.add(action);
                } catch (TemplateException | IOException e) {
                    log.error("freemarker template process filed.", e);
//                    throw new RuntimeException("freemarker template process filed. template: " + template.getName(), e);
                }
            }
        }
        return Collections.unmodifiableList(actionList);
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

        BusinessScenarioEnum businessScenario = BusinessScenarioEnum.ofCode(project.getBusinessScenario());
        if (businessScenario == null) {
            throw new ErrorParamException("The business scenario of the project is not legal.");
        }
        modelMap.addAttribute("businessScenario", businessScenario);
        return modelMap;
    }


}
