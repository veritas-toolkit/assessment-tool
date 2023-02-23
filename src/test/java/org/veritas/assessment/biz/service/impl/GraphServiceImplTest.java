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
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModelTestUtils;
import org.veritas.assessment.biz.service.ModelArtifactService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class GraphServiceImplTest {
    @Autowired
    private GraphServiceImpl graphService;

    @Autowired
    private ModelArtifactService modelArtifactService;

    @Test
    void testCreateAllGraph_success() throws Exception {
        final int projectId = 999;
        File jsonFile = new ClassPathResource(JsonModelTestUtils.EXAMPLE_PUW).getFile();
        byte[] content = null;
        String contentString = null;
        try (InputStream inputStream = Files.newInputStream(jsonFile.toPath())) {
            content = IOUtils.toByteArray(inputStream);
            contentString = new String(content, StandardCharsets.UTF_8);
        }

        String sha256 = DigestUtils.sha256Hex(content);

        File zipFile = new File("target/file/project/" + projectId + "/json/" + sha256 + ".json.zip");
        FileUtils.deleteDirectory(zipFile.getParentFile().getParentFile());

        if (!zipFile.exists()) {
            FileUtils.createParentDirectories(zipFile);
            File zipTemp = new File(zipFile.getParentFile(), FilenameUtils.getName(zipFile.getName()) + ".tmp");
            try (FileOutputStream fileOutputStream = new FileOutputStream(zipTemp)) {
                try (ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
                    ZipEntry zipEntry = new ZipEntry(FilenameUtils.getName(jsonFile.getName()));
                    zos.putNextEntry(zipEntry);
                    IOUtils.write(content, zos);
                    zos.closeEntry();
                }
            } catch (IOException ioException) {
                log.warn("Write file[{}] failed.", zipTemp.getPath(), ioException);
                throw ioException;
            }
            FileUtils.moveFile(zipTemp, zipFile);

        }

        log.info("jsonFile exist: {}", jsonFile.exists());
        log.info("zipFile path: {}", zipFile.getAbsolutePath());
        log.info("zipFile exist: {}", zipFile.exists());

        ModelArtifact artifact = new ModelArtifact();
        artifact.setProjectId(projectId);
        artifact.setJsonZipPath(zipFile.getName());
        artifact.setJsonModel(JsonModelTestUtils.load(JsonModelTestUtils.EXAMPLE_PUW));
        artifact.setJsonContentSha256(sha256);
        GraphContainer graphContainer = graphService.createAllGraph(artifact);
        assertNotNull(graphContainer);
        log.info("container:\n{}", graphContainer);
        assertFalse(graphContainer.getSummaryPlotMap().isEmpty());

//        FileUtils.deleteDirectory(zipFile.getParentFile().getParentFile());
    }

    @Test
    void name() {
        String imageFilename = "{sha256}_partialDependencePlot_{id}_{feature}.png";
        String basename = FilenameUtils.getBaseName(imageFilename);
        String[] sectionArray = StringUtils.split(basename, "_");
        String key = "default";
        String feature = "default";
        if (sectionArray.length >= 3) {
            key = sectionArray[2];
            if (sectionArray.length >= 4) {
                feature =String.join("_",
                        Arrays.copyOfRange(sectionArray, 3, sectionArray.length));
            }
        }
        log.info("key: {}", key);
        log.info("feature: {}", feature);
    }
}