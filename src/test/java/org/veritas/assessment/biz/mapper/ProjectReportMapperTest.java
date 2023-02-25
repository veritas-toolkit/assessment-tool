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

package org.veritas.assessment.biz.mapper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.ProjectReport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class ProjectReportMapperTest {

    @Autowired
    private ProjectReportMapper mapper;

    public static List<ProjectReport> data(int projectId, int count) {
        List<ProjectReport> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ProjectReport projectReport = new ProjectReport();
            projectReport.setProjectId(projectId);
            projectReport.setModelArtifactVid(2);
            projectReport.setQuestionnaireVid(3L);
            projectReport.setCreatorUserId(4);
            projectReport.setTag("V1.0");
            projectReport.setMessage("First version.");
            list.add(projectReport);
        }
        return Collections.unmodifiableList(list);

    }

    @Test
    void name() {
        assertNotNull(mapper);
        ProjectReport projectReport = new ProjectReport();
        projectReport.setProjectId(1);
        projectReport.setModelArtifactVid(2);
        projectReport.setQuestionnaireVid(3L);
        projectReport.setCreatorUserId(4);
        projectReport.setTag("V1.0");
        projectReport.setMessage("First version.");
        mapper.add(projectReport);
        assertNotNull(projectReport.getVersionId());
    }

    @Test
    void testFindByPidAndVid_success() {
        int projectId = 10;
        int count = 20;
        data(projectId, count).forEach(mapper::add);

        ProjectReport projectReport = mapper.findPidAndVid(projectId, 3);
        assertNotNull(projectReport);
        assertEquals(3, projectReport.getVersionIdOfProject());

        ProjectReport projectReport2 = mapper.findPidAndVid(projectId, 3);
        assertNotNull(projectReport2);


    }

    @Test
    void testFindByPidAndVid_success2() throws Exception {
        int projectId = RandomUtils.nextInt();
        int count = 20;
        data(projectId, count).forEach(mapper::add);
        ProjectReport projectReport1 = mapper.findPidAndVid(projectId, count + 1);
        assertNull(projectReport1);
        ProjectReport projectReport2 = mapper.findPidAndVid(projectId, count + 1);
        assertNull(projectReport2);
    }

    @Test
    void testFindAllByProjectId_success() throws Exception {
        int projectId = RandomUtils.nextInt();
        int count = 20;
        data(projectId, count).forEach(mapper::add);
        List<ProjectReport> list1 = mapper.findAllByProjectId(projectId);
        assertEquals(count, list1.size());
        log.info("load 1st");
        List<ProjectReport> list2 = mapper.findAllByProjectId(projectId);
        assertEquals(count, list2.size());
        log.info("load 2nd");

        data(projectId, count).forEach(mapper::add);
        List<ProjectReport> list3 = mapper.findAllByProjectId(projectId);
        log.info("load 3id");
        assertEquals(count + count, list3.size());

    }
}