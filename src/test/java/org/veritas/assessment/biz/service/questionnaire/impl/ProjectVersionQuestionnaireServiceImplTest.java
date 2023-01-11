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
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectVersionQuestionnaire;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.ProjectQuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire.ProjectVersionQuestionnaireService;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProjectVersionQuestionnaireServiceImplTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ProjectVersionQuestionnaireService service;
    @Autowired
    private ProjectQuestionnaireService questionnaireService;


    public Project createProject() {
        int userId = 1;
        User user = userService.findUserById(userId);
        Project project = new Project();
        project.setName("project_name");
        project.setDescription("description");
        project.setBusinessScenario(1);
        project.setUserOwnerId(userId);
        project = projectService.createProject(user, project, 1);
        return project;
    }

    private void fillAnswer(Project project) {
        ProjectQuestionnaire questionnaire = questionnaireService.findQuestionnaireById(project.getId());
        for (ProjectQuestion main : questionnaire.getQuestions()) {
            if (main.completed()) {
                continue;
            }
            for (ProjectQuestion projectQuestion : main.toList()) {
                projectQuestion.setAnswer("answer_" + RandomStringUtils.randomAlphanumeric(40));
                projectQuestion.setAnswerEditTime(new Date());
            }
            questionnaireService.editAnswer(project.getId(), main);
        }
        log.info("All answers filled.");
    }


    @Test
    void testCreate_success() {
        Project project = createProject();
        fillAnswer(project);
        ProjectVersionQuestionnaire version = service.create(project.getId());
        assertNotNull(version);
        assertNotNull(version.getVersionId());
    }

    @Test
    void testCreate_twiceSuccess() {
        Project project = createProject();
        fillAnswer(project);
        ProjectVersionQuestionnaire version = service.create(project.getId());
        ProjectVersionQuestionnaire version2 = service.create(project.getId());
        assertNotNull(version);
        assertNotNull(version.getVersionId());
        assertNotNull(version2);
        assertNotNull(version2.getVersionId());
        assertEquals(version.getVersionId(), version2.getVersionId());
    }

    @Test
    void testFindListProjectId_success() {
        Project project = createProject();
        fillAnswer(project);
        ProjectVersionQuestionnaire version = service.create(project.getId());
        fillAnswer(project);
        ProjectVersionQuestionnaire version2 = service.create(project.getId());

        List<ProjectVersionQuestionnaire> list = service.findListByProjectId(project.getId());
        log.info("list: {}", list);
        assertEquals(2, list.size());
    }
}