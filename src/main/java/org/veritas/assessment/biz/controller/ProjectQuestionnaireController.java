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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.QuestionnaireRecordDtoConverter;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionAnswerInputDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireRecordDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.SimpleQuestionDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/questionnaire")
public class ProjectQuestionnaireController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    QuestionnaireRecordDtoConverter questionnaireRecordDtoConverter;

    @Operation(summary = "Find the project's questionnaire history list.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/history")
    public Pageable<QuestionnaireRecordDto> findHistory(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestParam(name = "mainQuestionId", required = false) Long mainQuestionId,
            @RequestParam(name = "draftOnly", required = false) Boolean draftOnly,
            @RequestParam(name = "exportedOnly", required = false) Boolean exportedOnly,
            @RequestParam(name = "page", defaultValue = "1", required = false) Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20", required = false) Integer pageSize
    ) {
        Pageable<QuestionnaireVersion> page2 = questionnaireService.findHistory(projectId, page, pageSize);

        return questionnaireRecordDtoConverter.convertFrom(page2,
                (questionnaireRecordDto, questionnaireVersion) -> {
                    User user = userService.findUserById(questionnaireVersion.getCreatorUserId());
                    UserSimpleDto simpleDto = new UserSimpleDto(user);
                    questionnaireRecordDto.setCreator(simpleDto);
                });
    }

    // latest questionnaire table of contents.
    @Operation(summary = "Fetch project latest questionnaire table of contents.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/toc")
    public QuestionnaireTocDto findLatestToc(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId) {
        QuestionnaireVersion q = questionnaireService.findLatestQuestionnaire(projectId);
        if (q == null) {
            throw new NotFoundException("Not found the questionnaire.");
        }
        Project project = projectService.findProjectById(projectId);
        User user = userService.findUserById(q.getCreatorUserId());
        UserSimpleDto userSimpleDto = new UserSimpleDto(user);
        return new QuestionnaireTocDto(q, project, userSimpleDto);
    }


    @Operation(summary = "Fetch main question with all subs.")
    @GetMapping(path = "/question/{questionId}")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public QuestionDto findProjectQuestion(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Long questionId,
            @RequestParam(name = "questionnaireVid", required = false) Long questionnaireVid) {
        QuestionnaireVersion questionnaire;
        if (questionnaireVid == null) {
            questionnaire = questionnaireService.findLatestQuestionnaire(projectId);
        } else {
            questionnaire = questionnaireService.findByQuestionnaireVid(questionnaireVid);
        }
        if (questionnaire == null) {
            throw new NotFoundException("Not found the questionnaire.");
        }
        QuestionNode questionNode = questionnaire.findMainQuestionById(questionId);
        if (questionNode == null) {
            throw new NotFoundException("Not found the question in current project.");
        }
        return new QuestionDto(questionNode);
    }

    @Operation(summary = "Update answer of question, including sub question's answers.")
    @RequestMapping(path = "/answer", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'input answer')")
    public SimpleQuestionDto editAnswer(@Parameter(hidden = true) User operator,
                                        @PathVariable("projectId") Integer projectId,
                                        @RequestBody QuestionAnswerInputDto projectQuestion) {
        Objects.requireNonNull(projectQuestion);
        Objects.requireNonNull(projectQuestion.getQuestionId());
        Long questionId = projectQuestion.getQuestionId();

        QuestionnaireVersion questionnaire = questionnaireService.editAnswer(projectId,
                questionId, projectQuestion.getBasedQuestionVid(),
                projectQuestion.getAnswer(),
                operator.getId());
        QuestionNode questionNode = questionnaire.findNodeByQuestionId(questionId);
        return new SimpleQuestionDto(questionNode);
    }


}
