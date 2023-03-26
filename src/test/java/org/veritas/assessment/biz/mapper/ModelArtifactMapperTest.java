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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class ModelArtifactMapperTest {
    @Autowired
    private ModelArtifactMapper modelArtifactMapper;

    @Test
    void testAdd_success() {
        assertNotNull(modelArtifactMapper);
        ModelArtifact modelArtifact = new ModelArtifact();
        modelArtifact.setProjectId(8);
        modelArtifact.setFilename("filename");
        modelArtifact.setUploadUserId(9);
        modelArtifact.setUploadTime(new Date());
        String jsonContent = "fake json content";
        modelArtifact.setJsonContent(jsonContent);
        modelArtifact.setJsonContentSha256("fake_json_content_sha256");
        modelArtifact.setJsonZipPath("fake_json_zip_path");
        modelArtifactMapper.add(modelArtifact);

    }
}