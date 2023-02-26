package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.mapper.QuestionCommentMapper;
import org.veritas.assessment.biz.mapper.QuestionCommentReadLogMapper;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
import org.veritas.assessment.biz.service.CommentService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    @Autowired
    private QuestionCommentMapper commentMapper;
    @Autowired
    private QuestionCommentReadLogMapper commentReadLogMapper;
    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Autowired
    private ProjectService projectService;

    @Override
    public void addComment(QuestionComment comment) {
        Objects.requireNonNull(comment);
        Objects.requireNonNull(comment.getProjectId());
        Integer projectId = comment.getProjectId();
        QuestionnaireVersion questionnaireVersion = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionNode questionNode = questionnaireVersion.findNodeByQuestionId(comment.getQuestionId());
        if (questionNode == null) {
            throw new NotFoundException("Not fund the question in current project.");
        }
        comment.setMainQuestionId(questionNode.getMainQuestionId());
        int result = commentMapper.add(comment);
        if (result <= 0) {
            log.warn("Add comment failed.");
        }
        // mark all as read for current user.
        this.updateCommentReadLog(comment.getUserId(), projectId, comment.getQuestionId(), comment.getId());
    }

    @Override
    public List<QuestionComment> findAllUnreadCommentList(User operator) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(operator.getId());
        List<Project> projectList = projectService.findProjectList(operator.getId());
        List<QuestionComment> result = new ArrayList<>();
        for (Project project : projectList) {
            List<QuestionComment> commentList = commentMapper.findByProjectId(project.getId());
            Map<Long, QuestionCommentReadLog> logMap = commentReadLogMapper.findLog(operator.getId(), project.getId());
            commentList.stream()
                    .filter(comment -> {
                        comment.setProject(project);
                        QuestionCommentReadLog readLog = logMap.get(comment.getQuestionId());
                        return comment.isUnread(readLog);
                    })
                    .forEach(result::add);
        }
        return result;
    }

    @Override
    public List<QuestionComment> findCommentListByQuestionId(Long questionId) {
        return commentMapper.findByQuestionId(questionId);
    }

    @Override
    public List<QuestionComment> findCommentListByProjectId(Integer projectId) {
        Project project = projectService.findProjectById(projectId);
        if (project == null) {
            throw new NotFoundException("Not found the project");
        }
        List<QuestionComment> questionCommentList = commentMapper.findByProjectId(projectId);
        questionCommentList.forEach(comment -> comment.setProject(project));
        return questionCommentList;
    }

    @Override
    public Map<Long, QuestionCommentReadLog> findCommentReadLog(Integer userId, Integer projectId) {
        return commentReadLogMapper.findLog(userId, projectId);
    }

    @Override
    public void updateCommentReadLog(Integer userId, Integer projectId, Long questionId, Integer commentId) {
        QuestionnaireVersion questionnaire = questionnaireDao.findLatestQuestionnaire(projectId);
        ErrorParamException.requireNonNull(questionnaire, "Not found the questionnaire for project:" + projectId);
        QuestionNode questionNode = questionnaire.findNodeByQuestionId(questionId);
        ErrorParamException.requireNonNull(questionNode, "Not found the question.");

        ErrorParamException.requireNonNull(commentId, "Not fund the comment.");


        QuestionCommentReadLog readLog = commentReadLogMapper.findLog(userId, projectId).get(questionId);
        if (readLog != null) {
            int readCommentId = readLog.getLatestReadCommentId();
            if (readCommentId >= commentId) {
                return;
            } else {
                readLog.setLatestReadCommentId(commentId);
            }
        } else {
            readLog = new QuestionCommentReadLog();
            readLog.setUserId(userId);
            readLog.setProjectId(projectId);
            readLog.setQuestionId(questionId);
            readLog.setLatestReadCommentId(commentId);
        }
        int result = commentReadLogMapper.addOrUpdate(readLog);
        if (result <= 0) {
            log.warn("Update fail.");
        }

    }
}
