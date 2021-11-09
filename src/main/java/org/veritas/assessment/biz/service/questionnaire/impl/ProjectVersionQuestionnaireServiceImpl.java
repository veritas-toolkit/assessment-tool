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

package org.veritas.assessment.biz.service.questionnaire.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire.ProjectVersionQuestion;
import org.veritas.assessment.biz.entity.questionnaire.ProjectVersionQuestionnaire;
import org.veritas.assessment.biz.mapper.questionnaire.ProjectQuestionnaireDao;
import org.veritas.assessment.biz.mapper.questionnaire.ProjectVersionQuestionnaireDao;
import org.veritas.assessment.biz.service.questionnaire.AbstractQuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.ProjectVersionQuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.QuestionnaireNotCompletedException;

import java.util.List;

@Service
@Slf4j
public class ProjectVersionQuestionnaireServiceImpl
        extends AbstractQuestionnaireService<ProjectVersionQuestion, ProjectVersionQuestionnaire,
        ProjectVersionQuestionnaireDao>
        implements ProjectVersionQuestionnaireService {

    @Autowired
    private ProjectVersionQuestionnaireDao dao;

    @Autowired
    private ProjectQuestionnaireDao questionnaireDao;

    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;

    @Override
    @Transactional
    public ProjectVersionQuestionnaire create(Integer projectId) {
        ProjectQuestionnaire questionnaire = questionnaireDao.findByProjectId(projectId);
        if (questionnaire == null) {
            log.warn("There is no questionnaire of project[{}]", projectId);
            throw new QuestionnaireNotCompletedException("There is no questionnaire of the project.");
        } else if (!questionnaire.completed()) {
            log.warn("The questionnaire has not been completed. project[{}]", projectId);
            throw new QuestionnaireNotCompletedException("The questionnaire has not been completed.");
        }
        ProjectVersionQuestionnaire latest = dao.findLatestByProjectId(projectId);
        if (latest != null && !latest.updated(questionnaire)) {
            return latest;
        } else {
            ProjectVersionQuestionnaire version = ProjectVersionQuestionnaire.createFrom(questionnaire);
            dao.add(version);
            return version;
        }
    }

    @Override
    @Transactional
    public List<ProjectVersionQuestionnaire> findListByProjectId(Integer projectId) {
        return dao.findByProjectId(projectId);
    }
}