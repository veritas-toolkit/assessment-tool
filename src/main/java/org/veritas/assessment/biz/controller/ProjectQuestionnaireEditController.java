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
import org.apache.commons.lang3.StringUtils;
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
import org.veritas.assessment.biz.action.AddSubQuestionAction;
import org.veritas.assessment.biz.action.DeleteSubQuestionAction;
import org.veritas.assessment.biz.action.EditMainQuestionAction;
import org.veritas.assessment.biz.action.EditSubQuestionAction;
import org.veritas.assessment.biz.action.ReorderSubQuestionAction;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.dto.questionnaire.QuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionEditDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionReorderDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocWithMainQuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.SubQuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.SubQuestionEditDto;
import org.veritas.assessment.biz.dto.questionnaire.SubQuestionReorderDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.InternalException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import javax.validation.Valid;
import java.util.Objects;

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
                                                               @Valid @RequestBody QuestionAddDto dto) {
        Project project = projectService.findProjectById(projectId);
        dto.setProjectId(projectId);
        AddMainQuestionAction action = dto.toAction();
        action.setOperator(operator);
        QuestionnaireVersion questionnaire = questionnaireService.addMainQuestion(action);

        QuestionNode added = questionnaire.find(dto.getPrinciple(), dto.getStep()).stream()
                .filter(q -> StringUtils.equals(q.questionContent(), dto.getQuestion()))
                .findFirst()
                .orElse(null);
        if (added == null) {
            throw new InternalException("Create question failed.");
        }
        QuestionnaireTocDto questionnaireTocDto = new QuestionnaireTocDto(questionnaire, project, operator);
        QuestionDto questionDto = new QuestionDto(added);
        return new QuestionnaireTocWithMainQuestionDto(questionnaireTocDto, questionDto);
    }

    @Operation(summary = "Delete a main question with subs from the project's questionnaire.")
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

    @Operation(summary = "Edit a main question.")
    @PostMapping("/question/{questionId}")
    public QuestionnaireTocWithMainQuestionDto editMainQuestion(@Parameter(hidden = true) User operator,
                                                                @PathVariable("projectId") Integer projectId,
                                                                @PathVariable("questionId") Long questionId,
                                                                @Valid @RequestBody QuestionEditDto dto) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        if (!Objects.equals(questionId, dto.getQuestionId())) {
            throw new IllegalRequestException("The question IDs are different.");
        }
        EditMainQuestionAction action = new EditMainQuestionAction();
        action.setProjectId(projectId);
        action.setQuestionId(questionId);
        action.setQuestion(dto.getQuestion());
        action.setBasedQuestionVid(dto.getBasedQuestionVid());
        action.setOperator(operator);
        QuestionnaireVersion questionnaire = questionnaireService.editMainQuestion(action);
        QuestionNode main = questionnaire.findNodeByQuestionId(questionId);
        QuestionnaireTocDto questionnaireTocDto = new QuestionnaireTocDto(questionnaire, project, operator);
        QuestionDto questionDto = new QuestionDto(main);
        return new QuestionnaireTocWithMainQuestionDto(questionnaireTocDto, questionDto);
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


    @Operation(summary = "Add a sub question into the main question")
    @PostMapping("/question/{questionId}/sub/new")
    public QuestionDto addSubQuestion(@Parameter(hidden = true) User operator,
                                      @PathVariable("projectId") Integer projectId,
                                      @PathVariable("questionId") Long questionId,
                                      @Valid @RequestBody SubQuestionAddDto dto) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        AddSubQuestionAction action = new AddSubQuestionAction();
        action.setProjectId(projectId);
        action.setMainQuestionId(questionId);
        action.setSubQuestion(dto.getQuestion());
        action.setBeforeQuestionId(dto.getBeforeQuestionId());
        action.setOperator(operator);
        QuestionnaireVersion questionnaireVersion = questionnaireService.addSubQuestion(action);
        QuestionNode main = questionnaireVersion.findNodeByQuestionId(questionId);
        return new QuestionDto(main);
    }

    @Operation(summary = "Add a sub question into the main question")
    @DeleteMapping("/question/{questionId}/sub/{subQuestionId}")
    public QuestionDto deleteSubQuestion(@Parameter(hidden = true) User operator,
                                         @PathVariable("projectId") Integer projectId,
                                         @PathVariable("questionId") Long questionId,
                                         @PathVariable("subQuestionId") Long subQuestionId) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        DeleteSubQuestionAction action = new DeleteSubQuestionAction();
        action.setProjectId(projectId);
        action.setMainQuestionId(questionId);
        action.setSubQuestionId(subQuestionId);
        action.setOperator(operator);
        QuestionnaireVersion questionnaireVersion = questionnaireService.deleteSubQuestion(action);
        QuestionNode main = questionnaireVersion.findNodeByQuestionId(questionId);
        return new QuestionDto(main);
    }

    @Operation(summary = "Edit a sub question.")
    @PostMapping("/question/{questionId}/sub/{subQuestionId}")
    public QuestionDto editSubQuestion(@Parameter(hidden = true) User operator,
                                       @PathVariable("projectId") Integer projectId,
                                       @PathVariable("questionId") Long questionId,
                                       @PathVariable("subQuestionId") Long subQuestionId,
                                       @Valid @RequestBody SubQuestionEditDto dto) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        EditSubQuestionAction action = new EditSubQuestionAction();
        action.setProjectId(projectId);
        action.setMainQuestionId(questionId);
        action.setSubQuestion(dto.getQuestion());
        action.setSubQuestionId(dto.getQuestionId());
        action.setBasedSubQuestionVid(dto.getBasedQuestionVid());
        action.setOperator(operator);
        QuestionnaireVersion questionnaireVersion = questionnaireService.editSubQuestion(action);
        QuestionNode main = questionnaireVersion.findNodeByQuestionId(questionId);
        return new QuestionDto(main);
    }

    @Operation(summary = "Reorder sub question.")
    @PostMapping("/question/{questionId}/sub")
    public QuestionDto reorderSubQuestion(@Parameter(hidden = true) User operator,
                                          @PathVariable("projectId") Integer projectId,
                                          @PathVariable("questionId") Long questionId,
                                          @Valid @RequestBody SubQuestionReorderDto dto) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("The project not found.");
        }
        ReorderSubQuestionAction action = new ReorderSubQuestionAction();
        action.setProjectId(projectId);
        action.setMainQuestionId(questionId);
        action.setBasedQuestionnaireVid(dto.getBasedQuestionnaireVid());
        action.setOrderedSubQuestionIdList(dto.getOrderedSubQuestionIdList());
        action.setOperator(operator);
        QuestionnaireVersion questionnaireVersion = questionnaireService.reorderSubQuestion(action);
        QuestionNode main = questionnaireVersion.findNodeByQuestionId(questionId);
        return new QuestionDto(main);
    }

}
