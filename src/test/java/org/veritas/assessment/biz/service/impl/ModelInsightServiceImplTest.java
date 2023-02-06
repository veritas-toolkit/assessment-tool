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

package org.veritas.assessment.biz.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.JsonModelTest;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ModelInsightService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService1;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class ModelInsightServiceImplTest {

    @Autowired
    ProjectService projectService;
    @Autowired
    UserService userService;
    @Autowired
    ProjectQuestionnaireService1 questionnaireService;
    @Autowired
    ModelArtifactService modelArtifactService;
    @Autowired
    private ModelInsightService modelInsightService;

    @Autowired
    private TemplateQuestionnaireService templateQuestionnaireService;

    @Test
    @DisplayName("Auto generate answer")
    void testAutoGenerateAnswer_success() throws Exception {
        User admin = userService.findUserById(1);
        Project project = new Project();
        project.setUserOwnerId(admin.getId());
        project.setName("test-" + RandomStringUtils.randomAlphanumeric(5));
        project.setDescription("description-" + RandomStringUtils.randomAlphanumeric(10));
        project.setBusinessScenario(1);

        project = projectService.createProject(admin, project, templateQuestionnaireService.findByTemplateId(1));
        String jsonUrl = JsonModelTest.creditScoringUrl;
        String filename = FilenameUtils.getName(jsonUrl);
        String json = JsonModelTest.loadJson(jsonUrl);

        ModelArtifact modelArtifact = modelArtifactService.create(project.getId(), json, filename);
        modelArtifactService.saveJsonFile(modelArtifact);

        modelInsightService.autoGenerateAnswer(project, modelArtifact);

        ProjectQuestionnaire questionnaire = questionnaireService.findByProject(project.getId());

        long count = questionnaire.allQuestionsWithSub().stream()
                .filter(question -> StringUtils.isNotEmpty(question.getAnswer()))
                .count();
        assertTrue(count > 5);

        String target = "B4";
        int sub = 1;
        for (ProjectQuestion question : questionnaire.getQuestions()) {
            String title = question.title();
            if (!StringUtils.equalsIgnoreCase(title, target)) {
                continue;
            }
            if (sub == 0) {
                log.info("{} answer:\n{}", title, question.getAnswer());
            }
            for (ProjectQuestion subQuestion : question.getSubQuestions()) {
                if (subQuestion.getSubSerial() == sub) {
                    log.info("{}.{} answer:\n{}", title, subQuestion.getSubSerial(), subQuestion.getAnswer());
                }
            }
        }

    }


}