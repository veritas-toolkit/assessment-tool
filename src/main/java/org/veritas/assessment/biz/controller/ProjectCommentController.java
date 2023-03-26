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
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.QuestionCommentDtoConverter;
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.CommentService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.system.entity.User;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/")
public class ProjectCommentController {

    @Autowired
    private QuestionCommentDtoConverter commentDtoConverter;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private CommentService commentService;

    @GetMapping("/all_unread_comment_list")
    public List<QuestionCommentDto> allUnreadComment(User operator) {
        List<QuestionComment> list = commentService.findAllUnreadCommentList(operator);
        List<QuestionCommentDto> dtoList = commentDtoConverter.convertFrom(list);
        dtoList.forEach(d -> d.setHasRead(true));
        return dtoList.stream().sorted((a, b) -> b.getId().compareTo(a.getId())).collect(Collectors.toList());
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

    @Operation(summary = "Find all unread comment of the project.")
    @GetMapping(path = "/project/{projectId}/questionnaire/all_unread_comment_list")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<QuestionCommentDto> findUnreadCommentListByProject(@Parameter(hidden = true) User operator,
                                                                   @PathVariable("projectId") Integer projectId) {
        QuestionnaireVersion questionnaireVersion = questionnaireService.findLatestQuestionnaire(projectId);
        List<QuestionComment> list = commentService.findCommentListByProjectId(projectId);
        Map<Long, QuestionCommentReadLog> commentReadLogMap =
                commentService.findCommentReadLog(operator.getId(), projectId);
        List<QuestionCommentDto> dtoList = commentDtoConverter.convertFrom(list, e -> e.fillHasRead(commentReadLogMap));
        dtoList = dtoList.stream()
                .filter(e -> !e.isHasRead())
                .sorted((a, b) -> b.getId() - a.getId())
                .collect(Collectors.toList());
        dtoList.forEach(e -> {
            QuestionNode main = questionnaireVersion.findMainQuestionById(e.getMainQuestionId());
            if (main != null) {
                e.setMainQuestion(main.questionContent());
            }
        });
        return dtoList;
    }

    @Operation(summary = "Find all comment of project.")
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

    @Operation(summary = "Mark the comment as read.")
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
