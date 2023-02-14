package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.action.AddMainQuestionAction;
import org.veritas.assessment.biz.action.AddSubQuestionAction;
import org.veritas.assessment.biz.action.DeleteSubQuestionAction;
import org.veritas.assessment.biz.action.EditMainQuestionAction;
import org.veritas.assessment.biz.action.EditSubQuestionAction;
import org.veritas.assessment.biz.action.ReorderSubQuestionAction;
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

import javax.validation.Valid;
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
    public QuestionnaireVersion addMainQuestion(User operator, AddMainQuestionAction action) {
        Integer projectId = action.getProjectId();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);

        Date now = new Date();
        // lock questionnaire
        QuestionnaireVersion current = latest.createNewVersion(operator, now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }
        QuestionNode newNode = action.toNode(idGenerateService::nextId);
        current.addMainQuestion(newNode);

        // save question version & question meta
        boolean result = questionnaireDao.addNewQuestion(current, newNode);
        if (!result) {
            log.error("Add question failed, question: {}", newNode);
        }
        return latest;
    }

    @Override
    @Transactional
    public QuestionnaireVersion deleteMainQuestion(User operator, Project project, Long questionId) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(project);
        Objects.requireNonNull(questionId);
        Date now = new Date();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(project.getId());
        QuestionnaireVersion current = latest.createNewVersion(operator, now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(project.getId(), latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }

        QuestionNode delete = current.findMainQuestionById(questionId);
        if (delete == null) {
            log.warn("The question[{}] is not existing in current project.", questionId);
            return latest;
        }
        current.deleteMainQuestion(questionId);
        questionnaireDao.saveStructure(current);
        return current;
    }

    @Override
    public QuestionnaireVersion editMainQuestion(User operator, Project project, EditMainQuestionAction action) {
        Objects.requireNonNull(operator);
        Objects.requireNonNull(project);
        Objects.requireNonNull(action);
        // load latest
        Date now = new Date();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(project.getId());
        // create new version
        QuestionnaireVersion current = latest.createNewVersion(operator, now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(project.getId(), latest.getVid(), questionnaireVid);
        // lock
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }

        Long mainQuestionId = action.getMainQuestionId();
        QuestionNode mainNode = current.findMainQuestionById(action.getMainQuestionId());
        if (mainNode == null) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others. " +
                    "The main question has been deleted.");
        }

        // delete
        //      update meta
        //      delete node from new questionnaire
        questionnaireDao.deleteSubQuestion(current, mainNode.getQuestionId(), action.getDeletedSubList());
        current.deleteSubQuestions(mainNode.getMainQuestionId(), action.getDeletedSubList());


        // add new questions
            // insert meta
            // insert question-version
            // create new node into new questionnaire

//        List<QuestionNode> newNodeList = current.addSub(mainQuestionId, action.getAddSubQuestionList());

        // creat new version for edited question
            // update meta
            // insert question-version
        List<QuestionNode> editedNodeList = current.edite(mainQuestionId, action.toEditQuestionMap(),
                idGenerateService::nextId);

        // resort the node
        // insert new question node list.

        return null;
    }

    @Override
    @Transactional
    public QuestionnaireVersion reorderQuestion(User operator, Integer projectId, Principle principle,
                                                AssessmentStep step, List<Long> questionIdReorderList) {
        Date now = new Date();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(operator, now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }

        current.reorder(principle, step, questionIdReorderList);

        questionnaireDao.saveStructure(current);
        return current;
    }

    @Override
    @Transactional
    public QuestionnaireVersion addSubQuestion(@Valid AddSubQuestionAction action) {
        Date now = new Date();
        int projectId = action.getProjectId();

        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(action.getOperator(), now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }

        current.addSubQuestion(action, idGenerateService::nextId);

        questionnaireDao.save(current);
        return current;
    }

    @Override
    @Transactional
    public QuestionnaireVersion deleteSubQuestion(DeleteSubQuestionAction action) {
        Date now = new Date();
        int projectId = action.getProjectId();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(action.getOperator(), now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }
        current.deleteSubQuestion(action);

        questionnaireDao.save(current);
        return current;
    }

    @Override
    @Transactional
    public QuestionnaireVersion editSubQuestion(EditSubQuestionAction action) {
        Date now = new Date();
        int projectId = action.getProjectId();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(action.getOperator(), now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }
        current.editSubQuestion(action, idGenerateService::nextId);
        questionnaireDao.save(current);
        return current;
    }

    @Override
    @Transactional
    public QuestionnaireVersion reorderSubQuestion(ReorderSubQuestionAction action) {
        Date now = new Date();
        int projectId = action.getProjectId();
        QuestionnaireVersion latest = questionnaireDao.findLatestQuestionnaire(projectId);
        QuestionnaireVersion current = latest.createNewVersion(action.getOperator(), now, idGenerateService::nextId);
        Long questionnaireVid = current.getVid();
        boolean locked = projectMapper.updateQuestionnaireForLock(projectId, latest.getVid(), questionnaireVid);
        if (!locked) {
            throw new HasBeenModifiedException("The questionnaire has been modify by others.");
        }
        // do action
        current.reorderSubQuestion(action);
        questionnaireDao.save(current);
        return current;
    }
}
