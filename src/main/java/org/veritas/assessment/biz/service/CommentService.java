package org.veritas.assessment.biz.service;

import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;

import java.util.List;
import java.util.Map;

public interface CommentService {
    // comment
    void addComment(QuestionComment comment);

    // find comment list question
    List<QuestionComment> findCommentListByQuestionId(Long questionId);

    // find comment list by project
    List<QuestionComment> findCommentListByProjectId(Integer projectId);



    Map<Long, QuestionCommentReadLog> findCommentReadLog(Integer userId, Integer projectId);

    void updateCommentReadLog(Integer userId, Integer projectId, Long questionId, Integer commentId);

}
