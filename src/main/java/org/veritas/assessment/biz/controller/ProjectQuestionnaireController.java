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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.QuestionCommentDtoConverter;
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.dto.v1.questionnaire.QuestionnaireForProjectDto;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/questionnaire")
public class ProjectQuestionnaireController {

    @Autowired
    private ProjectQuestionnaireService questionnaireService;

    @Autowired
    private QuestionCommentDtoConverter commentDtoConverter;


    @Operation(summary = "Get the project's questionnaire.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("")
    public QuestionnaireForProjectDto<ProjectQuestion, ProjectQuestionnaire> findQuestionnaire(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestParam(name = "onlyWithFirstAnswer", defaultValue = "false") boolean onlyWithFirstAnswer
    ) {
        ProjectQuestionnaire questionnaire = questionnaireService.findByProject(projectId);
        if (questionnaire == null) {
            throw new NotFoundException("Not found the project's questionnaire. Please check the project's id.");
        }
        if (onlyWithFirstAnswer) {
            final String NO_CONTENT = " ";
            boolean isFirst = true;
            for (ProjectQuestion question : questionnaire.getQuestions()) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }
                if (!StringUtils.isEmpty(question.getAnswer())) {
                    question.setAnswer(NO_CONTENT);
                }
                for (ProjectQuestion subQuestion : question.getSubQuestions()) {
                    if (!StringUtils.isEmpty(subQuestion.getAnswer())) {
                        subQuestion.setAnswer(NO_CONTENT);
                    }
                }
            }
        }
        List<QuestionComment> list = questionnaireService.findCommentListByProjectId(projectId);
        Map<Integer, QuestionCommentReadLog> commentReadLogMap =
                questionnaireService.findCommentReadLog(operator.getId(), projectId);
        Map<Integer, List<QuestionCommentDto>> map =
                toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
        return new QuestionnaireForProjectDto<>(questionnaire, map);
    }

    @Operation(summary = "Get answer of question, including sub question's answers.")
    @GetMapping(path = "/question/{questionId}")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public ProjectQuestion findProjectQuestion(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Integer questionId) {
        ProjectQuestionnaire questionnaire = questionnaireService.findQuestionnaireById(projectId);
        ProjectQuestion projectQuestion = questionnaire.findQuestionById(questionId);
        if (projectQuestion == null) {
            throw new NotFoundException("Not found the question");
        }
        return projectQuestion;
    }

    @Operation(summary = "Update answer of question, including sub question's answers.")
    @RequestMapping(path = "/answer", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'input answer')")
    public ProjectQuestion editAnswer(@Parameter(hidden = true) User operator,
                                      @PathVariable("projectId") Integer projectId,
                                      @RequestBody ProjectQuestion projectQuestion) {
        projectQuestion.configQuestionnaireId(projectId);
        return questionnaireService.editAnswer(projectId, projectQuestion);
    }

    @Operation(summary = "Add a comment on the question.")
    @RequestMapping(path = "/comment", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<QuestionCommentDto> addComment(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionCommentCreateDto dto) {
        QuestionComment comment = dto.toEntity(operator.getId(), projectId);
        questionnaireService.addComment(comment);

        List<QuestionComment> list = questionnaireService.findCommentListByQuestionId(comment.getQuestionId());
        return commentDtoConverter.convertFrom(list);

    }

    @Operation(summary = "Find comment by project.")
    @GetMapping(path = "/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Integer, List<QuestionCommentDto>> findCommentListByProject(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId) {
        List<QuestionComment> list = questionnaireService.findCommentListByProjectId(projectId);
        Map<Integer, QuestionCommentReadLog> commentReadLogMap =
                questionnaireService.findCommentReadLog(operator.getId(), projectId);
        return toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
    }


    @Operation(summary = "Find comment by project or question.")
    @GetMapping(path = "/question/{questionId}/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Integer, List<QuestionCommentDto>> findCommentListByQuestionId(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Integer questionId) {
        List<QuestionComment> list = questionnaireService.findCommentListByQuestionId(questionId);
        for (QuestionComment comment : list) {
            if (!Objects.equals(projectId, comment.getProjectId())) {
                throw new ErrorParamException(String.format(
                        "The question[%d] does not belong to the project[%d]", questionId, projectId));
            }
        }
        Map<Integer, QuestionCommentReadLog> commentReadLogMap =
                questionnaireService.findCommentReadLog(operator.getId(), projectId);
        Optional<Integer> optional = list.stream().map(QuestionComment::getId).max(Integer::compareTo);
        if (optional.isPresent()) {
            Integer commentId = optional.get();
            questionnaireService.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
        }
        return toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
    }

    @Operation(summary = "Mark the comment read.")
    @RequestMapping(path = "/question/{questionId}/comment/{commentId}/read",
            method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public void markCommentRead(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Integer questionId,
            @PathVariable("commentId") Integer commentId) {
        questionnaireService.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
    }


    private Map<Integer, List<QuestionCommentDto>> toMapList(List<QuestionCommentDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Integer, List<QuestionCommentDto>> map = new TreeMap<>();
        for (QuestionCommentDto dto : dtoList) {
            List<QuestionCommentDto> qDtoList = map.computeIfAbsent(dto.getQuestionId(), k -> new ArrayList<>());
            qDtoList.add(dto);
        }
        return map;
    }

}
