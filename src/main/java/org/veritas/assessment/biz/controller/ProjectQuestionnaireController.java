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
import org.veritas.assessment.biz.converter.QuestionCommentDtoConverter;
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionEditDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.SimpleQuestionDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService1;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

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
    @Deprecated
    private ProjectQuestionnaireService1 questionnaireService1;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionCommentDtoConverter commentDtoConverter;

    @Autowired
    private QuestionnaireService questionnaireService;

    // 1. 获取问卷目录结构
    // 2. 获取指定问题（主问题）的题干和答案

    // 3. 修改答案
    // project_id, main_q_vid, sub_q_vid

    // comment 新增

    // comment 查询


    @Operation(summary = "Get the project's questionnaire.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/test")
    public QuestionnaireVersion findQuestionnaireTest(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestParam(name = "onlyWithFirstAnswer", defaultValue = "false") boolean onlyWithFirstAnswer) {
        QuestionnaireVersion q = questionnaireService.findLatestQuestionnaire(projectId);
        if (q == null) {
            throw new NotFoundException("");
        }
        return q;
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
            @PathVariable("questionId") Long questionId) {
        QuestionnaireVersion q = questionnaireService.findLatestQuestionnaire(projectId);
        if (q == null) {
            throw new NotFoundException("Not found the questionnaire.");
        }
        QuestionNode questionNode = q.findMainQuestionById(questionId);
        if (questionNode == null) {
            throw new NotFoundException("Not found the question in current project.");
        }
//        List<Integer> userIdList = new ArrayList<>();

//        UserSimpleDto questionEditor = userService.findUserById(questionNode.getQuestionVersion().getAnswerEditUserId());
        return new QuestionDto(questionNode);
    }

    @Operation(summary = "Update answer of question, including sub question's answers.")
    @RequestMapping(path = "/answer", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'input answer')")
    public SimpleQuestionDto editAnswer(@Parameter(hidden = true) User operator,
                                        @PathVariable("projectId") Integer projectId,
                                        @RequestBody QuestionEditDto projectQuestion) {
        Objects.requireNonNull(projectQuestion);
        Objects.requireNonNull(projectQuestion.getQuestionId());
        Long questionId = projectQuestion.getQuestionId();

        QuestionnaireVersion questionnaire = questionnaireService.editAnswer(projectId,
                questionId, projectQuestion.getBaseQuestionVid(),
                projectQuestion.getAnswer(),
                operator.getId());
        QuestionNode questionNode = questionnaire.findNodeByQuestionId(questionId);
        return new SimpleQuestionDto(questionNode);
    }

    @Operation(summary = "Add a comment on the question.")
    @RequestMapping(path = "/comment", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<QuestionCommentDto> addComment(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionCommentCreateDto dto) {
        QuestionComment comment = dto.toEntity(operator.getId(), projectId);
        questionnaireService1.addComment(comment);

        List<QuestionComment> list = questionnaireService1.findCommentListByQuestionId(comment.getQuestionId());
        return commentDtoConverter.convertFrom(list);
    }

    @Operation(summary = "Find comment by project.")
    @GetMapping(path = "/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Integer, List<QuestionCommentDto>> findCommentListByProject(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId) {
        List<QuestionComment> list = questionnaireService1.findCommentListByProjectId(projectId);
        Map<Integer, QuestionCommentReadLog> commentReadLogMap =
                questionnaireService1.findCommentReadLog(operator.getId(), projectId);
        return toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
    }


    @Operation(summary = "Find comment by project or question.")
    @GetMapping(path = "/question/{questionId}/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Integer, List<QuestionCommentDto>> findCommentListByQuestionId(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Integer questionId) {
        List<QuestionComment> list = questionnaireService1.findCommentListByQuestionId(questionId);
        for (QuestionComment comment : list) {
            if (!Objects.equals(projectId, comment.getProjectId())) {
                throw new ErrorParamException(String.format(
                        "The question[%d] does not belong to the project[%d]", questionId, projectId));
            }
        }
        Map<Integer, QuestionCommentReadLog> commentReadLogMap =
                questionnaireService1.findCommentReadLog(operator.getId(), projectId);
        Optional<Integer> optional = list.stream().map(QuestionComment::getId).max(Integer::compareTo);
        if (optional.isPresent()) {
            Integer commentId = optional.get();
            questionnaireService1.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
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
        questionnaireService1.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
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
