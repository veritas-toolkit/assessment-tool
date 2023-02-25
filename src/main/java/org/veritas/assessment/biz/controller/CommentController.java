package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.QuestionCommentDtoConverter;
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.service.CommentService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.entity.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@RestController
@Slf4j
@RequestMapping("/")
public class CommentController {

    @Autowired
    private QuestionCommentDtoConverter commentDtoConverter;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/all_unread_comment_list")
    public List<QuestionCommentDto> allUnreadComment(User operator) {

        List<QuestionCommentDto> list = new ArrayList<>();
        for (int i = 1; i <= 10; ++i) {
            QuestionCommentDto dto = new QuestionCommentDto();
            dto.setId(i);
            dto.setComment("comment: " + i);
            dto.setUsername("username_" + i);
            dto.setHasRead(false);
            dto.setProjectId(i);
            dto.setProjectName("project_" + i);
            dto.setProjectName("project_owner_" + i);
            dto.setUserFullName("user full name " + i);
            dto.setMainQuestionId((long) i);
            dto.setQuestionId(2L + i);
            dto.setCreatedTime(DateUtils.addMinutes(new Date(), -10 * i));
            list.add(dto);
        }
        return list;
    }

    @Operation(summary = "Add a comment on the question.")
    @RequestMapping(path = "/project/{projectId}/questionnaire/comment", method = {RequestMethod.PUT, RequestMethod.POST})
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<QuestionCommentDto> addComment(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody QuestionCommentCreateDto dto) {
        QuestionComment comment = dto.toEntity(operator.getId(), projectId);
        commentService.addComment(comment);

        List<QuestionComment> list = commentService.findCommentListByQuestionId(comment.getQuestionId());
        List<QuestionCommentDto> dtoList = commentDtoConverter.convertFrom(list);
        dtoList.forEach(d -> d.setHasRead(true));
        return dtoList;
    }

    @Operation(summary = "Find comment by project.")
    @GetMapping(path = "/project/{projectId}/questionnaire/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Long, List<QuestionCommentDto>> findCommentListByProject(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId) {
        List<QuestionComment> list = commentService.findCommentListByProjectId(projectId);
        Map<Long, QuestionCommentReadLog> commentReadLogMap =
                commentService.findCommentReadLog(operator.getId(), projectId);
        return QuestionCommentDto.toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
    }


    @Operation(summary = "Find comment by project or question.")
    @GetMapping(path = "/project/{projectId}/questionnaire/question/{questionId}/comment")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public Map<Long, List<QuestionCommentDto>> findCommentListByQuestionId(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Long questionId) {
        List<QuestionComment> list = commentService.findCommentListByQuestionId(questionId);
        for (QuestionComment comment : list) {
            if (!Objects.equals(projectId, comment.getProjectId())) {
                throw new ErrorParamException(String.format(
                        "The question[%d] does not belong to the project[%d]", questionId, projectId));
            }
        }
        Map<Long, QuestionCommentReadLog> commentReadLogMap =
                commentService.findCommentReadLog(operator.getId(), projectId);
        Optional<Integer> optional = list.stream().map(QuestionComment::getId).max(Integer::compareTo);
        if (optional.isPresent()) {
            Integer commentId = optional.get();
            commentService.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
        }
        return QuestionCommentDto.toMapList(commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap)));
    }

    @Operation(summary = "Mark the comment read.")
    @RequestMapping(path = "/project/{projectId}/questionnaire/question/{questionId}/comment/{commentId}/read",
            method = {RequestMethod.POST, RequestMethod.PUT})
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public void markCommentRead(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @PathVariable("questionId") Long questionId,
            @PathVariable("commentId") Integer commentId) {
        commentService.updateCommentReadLog(operator.getId(), projectId, questionId, commentId);
    }


}
