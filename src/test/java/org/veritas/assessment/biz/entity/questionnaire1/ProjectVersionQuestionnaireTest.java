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

package org.veritas.assessment.biz.entity.questionnaire1;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
public class ProjectVersionQuestionnaireTest {

    public static ProjectVersionQuestionnaire create(Integer projectId) {
        ProjectQuestionnaire questionnaire = ProjectQuestionnaireTest.create(projectId);
        ProjectVersionQuestionnaire versionQuestionnaire = ProjectVersionQuestionnaire.createFrom(questionnaire);
        return versionQuestionnaire;

    }

    @Test
    void nameCreateFrom_success() {
        int projectId = 1;

        ProjectQuestionnaire questionnaire = ProjectQuestionnaireTest.create(projectId);
        ProjectVersionQuestionnaire versionQuestionnaire = ProjectVersionQuestionnaire.createFrom(questionnaire);

        assertNull(versionQuestionnaire.getVersionId());
        assertEquals(questionnaire.getProjectId(), versionQuestionnaire.getProjectId());
        log.info("version:\n{}", versionQuestionnaire);

    }
}