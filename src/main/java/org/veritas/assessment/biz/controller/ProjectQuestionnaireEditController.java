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
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.dto.QuestionDto;
import org.veritas.assessment.biz.dto.v1.questionnaire.QuestionnaireDto;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService1;
import org.veritas.assessment.common.exception.ErrorParamException;

/**
 * Edit the questions of project's questionnaire.
 */
@RestController
@Slf4j
@RequestMapping("/project/{projectId}/questionnaire/edit")
@PreAuthorize("hasPermission(#projectId, 'project', 'edit questionnaire')")
public class ProjectQuestionnaireEditController {

    @Autowired
    private ProjectQuestionnaireService1 service;


    @Operation(summary = "Get the project's questionnaire without any answer.")
    @GetMapping("")
    public QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire> get(@PathVariable("projectId") Integer projectId) {
        ProjectQuestionnaire questionnaire = service.findQuestionnaireById(projectId);
        for (ProjectQuestion question : questionnaire.getQuestions()) {
            if (!StringUtils.isEmpty(question.getAnswer())) {
                question.setAnswer(null);
            }
            for (ProjectQuestion subQuestion : question.getSubQuestions()) {
                if (!StringUtils.isEmpty(subQuestion.getAnswer())) {
                    subQuestion.setAnswer(null);
                }
            }
        }
        return new QuestionnaireDto<>(questionnaire);
    }

    // add question
    @Operation(summary = "Add a question with subs into the project's questionnaire.")
    @PutMapping("/question")
    public ProjectQuestion addMainQuestion(
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionDto dto) {
        ProjectQuestion question = new ProjectQuestion();
        question.copyFrom(dto, ProjectQuestion::new);
        question.setProjectId(projectId);
        question.setSubSerial(0);
        return service.addMainQuestion(projectId, question);
    }

    // edit question
    @Operation(summary = "Edit a question of the project's questionnaire.")
    @PostMapping("/question")
    public ProjectQuestion editMainQuestion(
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionDto dto) {
        ProjectQuestion question = new ProjectQuestion();
        question.copyFrom(dto, ProjectQuestion::new);
        question.setProjectId(projectId);
        question.setSubSerial(0);
        int subSerial = 1;
        for (ProjectQuestion subQuestion : question.getSubQuestions()) {
            subQuestion.setProjectId(projectId);
            subQuestion.setSubSerial(subSerial);
            ++subSerial;
        }
        return service.updateMainQuestionWithSub(projectId, question);
    }

    // delete question
    @Operation(summary = "Delete a question(main/sub) from the project's questionnaire.")
    @DeleteMapping("/question")
    public QuestionnaireDto<ProjectQuestion, ProjectQuestionnaire> deleteQuestion(
            @PathVariable("projectId") Integer projectId,
            @RequestParam(name = "questionId", required = false) Integer questionId,
            @RequestParam(name = "subQuestionId", required = false) Integer subQuestionId) {
        if (subQuestionId != null) {
            service.deleteSubQuestion(projectId, subQuestionId);
        } else if (questionId != null) {
            service.deleteMainQuestion(projectId, questionId);
        } else {
            throw new ErrorParamException(
                    "Error param: At least one value exists for [questionId] and [subQuestionId]");
        }
        ProjectQuestionnaire questionnaire = service.findQuestionnaireById(projectId);
        return new QuestionnaireDto<>(questionnaire);
    }

}
