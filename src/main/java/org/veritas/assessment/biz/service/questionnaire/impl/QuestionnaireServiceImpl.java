package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
import org.veritas.assessment.biz.service.IdGenerateService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.HasBeenModifiedException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireDao questionnaireDao;

    @Autowired
    private IdGenerateService idGenerateService;
    @Autowired
    private ProjectMapper projectMapper;
    @Override
    @Transactional
    public QuestionnaireVersion findLatestQuestionnaire(int projectId) {
        return questionnaireDao.findLatestQuestionnaire(projectId);
    }

    @Transactional
    @Override
    public QuestionnaireVersion findByQuestionnaireVid(long vid) {
        return questionnaireDao.findByQuestionnaireVid(vid);
    }


    @Override
    public QuestionnaireVersion copyFrom(int creatorUserId, Project project, Date createdTime, Project fromProject) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(project.getId());
        Objects.requireNonNull(fromProject);
        Objects.requireNonNull(fromProject.getBusinessScenario());
        Objects.requireNonNull(fromProject.getId());

        boolean sameBiz = Objects.equals(project.getBusinessScenario(), fromProject.getBusinessScenario());
        if (!sameBiz) {
            throw new IllegalArgumentException();
        }
        QuestionnaireVersion oldQ = questionnaireDao.findLatestQuestionnaire(fromProject.getId());
        QuestionnaireVersion questionnaire = new QuestionnaireVersion(creatorUserId, project.getId(),
                createdTime, oldQ, idGenerateService::nextId);
        questionnaire.setMessage("Created.");

        return questionnaire;
    }

    @Override
    public QuestionnaireVersion createByTemplate(int creatorUserId, Project project, Date createdTime, TemplateQuestionnaire template) {
        Objects.requireNonNull(project, "Arg[project] should not be null.");
        Objects.requireNonNull(project.getBusinessScenario(),
                "Arg[project] businessScenario should not be null.");
        Objects.requireNonNull(template);

        boolean sameBiz = project.getBusinessScenario() == template.getBusinessScenario();
        if (!sameBiz) {
            throw new IllegalArgumentException();
        }
        QuestionnaireVersion questionnaire =
                new QuestionnaireVersion(creatorUserId, project.getId(), createdTime, template, idGenerateService::nextId);
        questionnaire.setMessage("Created.");

        return questionnaire;
    }

    @Override
    @Transactional
    public void saveNewQuestionnaire(QuestionnaireVersion questionnaireVersion) {
        questionnaireDao.saveNewQuestionnaire(questionnaireVersion);
    }

    @Override
    @Transactional
    public QuestionnaireVersion editAnswer(int projectId, long questionId, long basedQuestionVid, String answer, int editorId)
            throws HasBeenModifiedException {
        Date now = new Date();

        long questionNewVid = idGenerateService.nextId();
        // lock question-meta
        boolean questionLocked = questionnaireDao.updateQuestionVidForLock(questionId, basedQuestionVid, questionNewVid);
        if (!questionLocked) {
            throw new HasBeenModifiedException("The question has been modify by others.");
        }


        long questionnaireNewVid = idGenerateService.nextId();
        QuestionnaireVersion questionnaire = questionnaireDao.findLatestQuestionnaire(projectId);
        // lock
        // update project table.
        boolean questionnaireLocked = projectMapper.updateQuestionnaireForLock(projectId,
                questionnaire.getVid(), questionnaireNewVid);
        if (!questionnaireLocked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }


        // load latestQuestionnaire
        QuestionNode node = questionnaire.findNodeByQuestionId(questionId);
        if (node == null) {
            throw new NotFoundException("Not found the question.");
        }

        QuestionVersion questionVersion = node.getQuestionVersion();
        // update question's vid
        questionVersion.setVid(questionNewVid);
        node.setQuestionnaireVid(questionNewVid);

        questionVersion.setAnswer(answer);
        questionVersion.setAnswerEditUserId(editorId);
        questionVersion.setAnswerEditTime(now);
        node.updateQuestionVersion(questionVersion);

        questionnaire.configureQuestionnaireVid(questionnaireNewVid);
        questionnaire.setCreatorUserId(editorId);
        questionnaire.setCreatedTime(now);
        questionnaire.setMessage(String.format("Update question[%s] answer.", node.serial()));
        boolean createdSuccess = questionnaireDao.createNewVersion(questionnaire, questionVersion);
        if (!createdSuccess) {
            throw new IllegalStateException("Edit answer failed.");
        }
        return questionnaire;
    }

    @Override
    public Pageable<QuestionnaireVersion> findHistory(int projectId, int page, int pageSize) {
        return questionnaireDao.findHistoryPageable(projectId, page, pageSize);
    }

    @Override
    @Transactional
    public QuestionnaireVersion addMainQuestion(AddMainQuestionAction action) {
        Integer projectId = action.getProjectId();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);

        // lock questionnaire
        Long questionnaireVid = idGenerateService.nextId();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }
        // create node

        QuestionnaireVersion newQuestionnaire = latest.createNewVersion(idGenerateService::nextId);
        newQuestionnaire.configureQuestionnaireVid(questionnaireVid);
        QuestionNode newNode = action.toNode(idGenerateService::nextId);
        newQuestionnaire.addMainQuestion(newNode);

        // save question version & question meta
        boolean result = questionnaireDao.addNewQuestion(newQuestionnaire, newNode);
        if (!result) {
            log.error("Add question failed, question: {}", newNode);
        }
        return latest;
    }

    @Override
    @Transactional
    public QuestionnaireVersion reorderQuestion(User operator, Integer projectId, Principle principle,
                                                AssessmentStep step, List<Long> questionIdReorderList) {
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(idGenerateService::nextId);
        current.setCreatedTime(new Date());
        current.setCreatorUserId(operator.getId());
        current.reorder(principle, step, questionIdReorderList);

        questionnaireDao.saveStructure(current);
        return current;
    }
}
