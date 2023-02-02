package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireDao;
import org.veritas.assessment.biz.service.IdGenerateService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;

import java.util.Date;
import java.util.List;
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

    private List<QuestionMeta> createQuestionMeta() {
        return null;
    }

    private List<QuestionMeta> createQS() {
        return null;
    }

    private List<QuestionVersion> createQV() {
        return null;
    }
}
