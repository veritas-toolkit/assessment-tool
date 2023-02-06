package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.ProjectMapper;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
import org.veritas.assessment.biz.service.IdGenerateService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.HasBeenModifiedException;
import org.veritas.assessment.common.exception.NotFoundException;

import java.util.Date;
import java.util.Objects;

@Service
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;

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

    @Override
    public QuestionnaireVersion copyFrom(int projectId, int fromProject) {
        return null;
    }

    @Override
    public QuestionnaireVersion createByTemplate(int creatorUserId, Project project, Date createdTime, int templateId) {
        Objects.requireNonNull(project, "Arg[project] should not be null.");
        Objects.requireNonNull(project.getBusinessScenario(),
                "Arg[project] businessScenario should not be null.");

        TemplateQuestionnaire template = templateQuestionnaireService.findByTemplateId(templateId);
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
        if (questionLocked) {
            throw new HasBeenModifiedException("The question has been modify by others.");
        }


        long questionnaireNewVid = idGenerateService.nextId();
        // lock
        // update project table.
        boolean questionnaireLocked = projectMapper.updateQuestionnaireForLock(projectId, questionnaireNewVid);


        // load latestQuestionnaire
        QuestionnaireVersion questionnaire = questionnaireDao.findLatestQuestionnaire(projectId);
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

        questionnaire.updateQuestionnaireVid(questionnaireNewVid);
        questionnaire.setCreatorUserId(editorId);
        questionnaire.setCreatedTime(now);
        questionnaire.setMessage(String.format("Update question[%s] answer.", node.serial()));
        boolean createdSuccess = questionnaireDao.createNewVersion(questionnaire, questionVersion);
        if (!createdSuccess) {
            throw new IllegalStateException("Edit answer failed.");
        }
        return questionnaire;
    }
}
