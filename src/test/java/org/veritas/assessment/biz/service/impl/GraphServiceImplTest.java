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
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class GraphServiceImplTest {
    @Autowired
    private GraphServiceImpl graphService;

    @Test
    void testCreateAllGraph_success() throws Exception {
        final int projectId = 999;
        final String jsonZip = "14b48a23d5ecfcd2d711adf6d703bb353ba941bad1fe150709924986157aa4c0.json.zip";
        File file = new ClassPathResource("json/" + jsonZip).getFile();
        File newFile = new File("target/file/project/" + projectId + "/json/" + jsonZip);
        FileUtils.copyFile(file, newFile);
        log.info("file exist: {}", file.exists());
        log.info("new file path: {}", newFile.getAbsolutePath());
        log.info("new file exist: {}", newFile.exists());

        ModelArtifact artifact = new ModelArtifact();
        artifact.setProjectId(projectId);
        artifact.setJsonZipPath(jsonZip);

        GraphContainer graphContainer = graphService.createAllGraph(artifact);
        assertNotNull(graphContainer);
        log.info("container:\n{}", graphContainer);
        FileUtils.deleteDirectory(newFile.getParentFile().getParentFile());
    }
}