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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.dto.v1.questionnaire.QuestionnaireDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionAddDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionDto;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

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

    // fetch toc

    // fetch mian-question wit subs

    // add question

    // add sub-question

    // edit question-content, main or sub

    // change sub-q seq in main-q

    // change main-q seq in a principle

    // delete question, main or sub

    @Autowired
    UserService userService;

    // add question
    @Operation(summary = "Add a main question with subs into the project's questionnaire.")
    @PostMapping("/question/new")
    public QuestionDto addMainQuestion(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionAddDto dto) {
        dto.setProjectId(projectId);
        AddMainQuestionAction action = dto.toAction();
        action.setCreator(operator);
        questionnaireService.addMainQuestion(action);

        return null;
    }
}
