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

package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionAddDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionEditContentDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionReorderDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocWithMainQuestionDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;

/**
 * Edit the questions of project's questionnaire.
 */
@RestController
@Slf4j
@RequestMapping("/project/{projectId}/questionnaire/edit")
@PreAuthorize("hasPermission(#projectId, 'project', 'edit questionnaire')")
public class ProjectQuestionnaireEditController {


    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    UserService userService;

    @Autowired
    private ProjectService projectService;

    // add question
    @Operation(summary = "Add a main question with subs into the project's questionnaire.")
    @PostMapping("/question/new")
    public QuestionnaireTocWithMainQuestionDto addMainQuestion(@Parameter(hidden = true) User operator,
                                                               @PathVariable("projectId") Integer projectId,
                                                               @RequestBody QuestionAddDto dto) {
        Project project = projectService.findProjectById(projectId);
        dto.setProjectId(projectId);
        AddMainQuestionAction action = dto.toAction();
        action.setCreator(operator);
        QuestionnaireVersion questionnaire = questionnaireService.addMainQuestion(operator, action);
        QuestionnaireTocDto questionnaireTocDto = new QuestionnaireTocDto(questionnaire, project, operator);

        return null;
    }

    @Operation(summary = "Add a main question with subs into the project's questionnaire.")
    @DeleteMapping("/question/{questionId}")
    public QuestionnaireTocDto deleteMainQuestion(@Parameter(hidden = true) User operator,
                                                  @PathVariable("projectId") Integer projectId,
                                                  @PathVariable("questionId") Long questionId) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        QuestionnaireVersion questionnaire = questionnaireService.deleteMainQuestion(operator, project, questionId);
        return new QuestionnaireTocDto(questionnaire, project, operator);
    }


    // edit main question, including:
    //  a. edit main's content
    //  b. edit sub's content
    //  c. add new sub questions
    //  d. delete sub questions
    //  e. reorder sub questions
    @Operation(summary = "Add a main question with subs into the project's questionnaire.")
    @PostMapping("/question/{questionId}")
    public QuestionDto editMainQuestion(@Parameter(hidden = true) User operator,
                                        @PathVariable("projectId") Integer projectId,
                                        @PathVariable("questionId") Long questionId,
                                        @Valid @RequestBody QuestionEditContentDto dto) {

        return null;
    }

    @Operation(summary = "Reorder the sequence of the main questions.")
    @RequestMapping(path = "/reorder", method = {RequestMethod.POST, RequestMethod.PUT})
    public QuestionnaireTocDto reorder(@Parameter(hidden = true) User operator,
                                       @PathVariable("projectId") Integer projectId,
                                       @Valid @RequestBody QuestionReorderDto dto) {
        Principle principle = dto.getPrinciple();
        if (principle == null) {
            throw new IllegalRequestException();
        }
        Project project = projectService.findProjectById(projectId);
        dto.setProjectId(projectId);
        QuestionnaireVersion questionnaire = questionnaireService.reorderQuestion(operator, projectId,
                dto.getPrinciple(), dto.getStep(),
                dto.getQuestionIdList());
        return new QuestionnaireTocDto(questionnaire, project, operator);
    }

}
