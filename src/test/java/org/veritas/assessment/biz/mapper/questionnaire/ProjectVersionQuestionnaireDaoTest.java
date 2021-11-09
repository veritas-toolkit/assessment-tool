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

package org.veritas.assessment.biz.mapper.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.ProjectVersionQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire.ProjectVersionQuestionnaireTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
@Transactional
@Slf4j
@ActiveProfiles("test")
class ProjectVersionQuestionnaireDaoTest {
    @Autowired
    ProjectVersionQuestionnaireDao dao;

    @Test
    void testAdd_success() {
        int projectId = 1;
        ProjectVersionQuestionnaire versionQuestionnaire = ProjectVersionQuestionnaireTest.create(projectId);
        assertNull(versionQuestionnaire.getVersionId());
        dao.add(versionQuestionnaire);
        assertNotNull(versionQuestionnaire.getVersionId());
        assertEquals(projectId, versionQuestionnaire.getProjectId());
    }

    @Test
    void testFindByProjectId_success() {
        int projectId = 101;
        int versionCount = 10;
        for (int i = 1; i <= versionCount; ++i) {
            ProjectVersionQuestionnaire versionQuestionnaire = ProjectVersionQuestionnaireTest.create(projectId);
            assertNull(versionQuestionnaire.getVersionId());
            dao.add(versionQuestionnaire);
        }
        List<ProjectVersionQuestionnaire> list = dao.findByProjectId(projectId);
        assertEquals(versionCount, list.size());
        ProjectVersionQuestionnaire last = list.get(0);
        assertEquals(versionCount, last.getVersionId());
        assertEquals(projectId, last.getProjectId());
        log.info("last version:\n{}", last);
    }

    @Test
    void testFindLatestByProjectId_nullSuccess() {

        int projectId = 108;
        ProjectVersionQuestionnaire latest = dao.findLatestByProjectId(projectId);
        assertNull(latest);
    }


    @Test
    void testFindLatestyProjectId_success() {
        int projectId = 1010;
        int versionCount = 100;
        for (int i = 1; i <= versionCount; ++i) {
            ProjectVersionQuestionnaire versionQuestionnaire = ProjectVersionQuestionnaireTest.create(projectId);
            assertNull(versionQuestionnaire.getVersionId());
            dao.add(versionQuestionnaire);
        }
        ProjectVersionQuestionnaire latest = dao.findLatestByProjectId(projectId);
        assertEquals(versionCount, latest.getVersionId());
        assertEquals(projectId, latest.getProjectId());
        log.info("latest version:\n{}", latest);
    }

}