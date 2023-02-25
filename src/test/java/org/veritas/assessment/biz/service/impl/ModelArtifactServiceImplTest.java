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
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModelTestUtils;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@DisplayNameGeneration(DisplayNameGenerator.Standard.class)
class ModelArtifactServiceImplTest {
    @Autowired
    private ModelArtifactService service;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private VeritasProperties veritasProperties;

    @Test
    void testUpload_success() throws IOException {
        int projectId = 101;
        String filePath = JsonModelTestUtils.EXAMPLE_CS;
        String jsonFilename = FilenameUtils.getName(filePath);
        String content = IOUtils.toString(new ClassPathResource(filePath).getURL(), StandardCharsets.UTF_8);
        String sha256 = DigestUtils.sha256Hex(content);
        log.info("sha256: {}", sha256);
        ModelArtifact artifact = new ModelArtifact();
        artifact.setProjectId(projectId);
        artifact.setUploadUserId(1);
        artifact.setFilename(jsonFilename);
        artifact.setJsonContent(content);
        service.upload(artifact);
        assertEquals(sha256, artifact.getJsonContentSha256());
        String jsonZipPath = artifact.getJsonZipPath();
        assertNotNull(jsonZipPath);
        log.info("file path: {}", jsonZipPath);
    }

    @AfterEach
    void tearDown() throws IOException {
        FileUtils.cleanDirectory(new File(veritasProperties.getFilePath()));
    }

}