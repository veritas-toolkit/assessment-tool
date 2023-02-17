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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.Fairness;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.biz.mapper.ModelArtifactMapper;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.FileSystemException;
import org.veritas.assessment.system.config.VeritasProperties;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

@Service
@Slf4j
public class ModelArtifactServiceImpl implements ModelArtifactService {
    private final ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private ModelArtifactMapper modelArtifactMapper;

    @Autowired
    private VeritasProperties veritasProperties;

    @PostConstruct
    public void init() {
        objectMapper.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public JsonModel parser(String json) {
        try {
            return objectMapper.readValue(json, JsonModel.class);
        } catch (Exception exception) {
            log.warn("Cannot parse the json file.", exception);
            throw new ErrorParamException("Cannot parse the json file.");
        }
    }

    @Override
    public ModelArtifact create(Integer projectId, String json, String filename) {
        Objects.requireNonNull(projectId);
        if (StringUtils.isEmpty(json)) {
            throw new IllegalArgumentException("Argument 'json' should not be empty.");
        }
        if (StringUtils.isEmpty(filename)) {
            throw new IllegalArgumentException("Argument 'filename' should not be empty.");
        }
        ModelArtifact artifact = new ModelArtifact();
        artifact.setProjectId(projectId);
        artifact.setFilename(filename);
        artifact.setJsonContent(json);
        JsonModel jsonModel = parser(json);
        artifact.setJsonModel(jsonModel);
        return artifact;
    }

    @Override
    @Transactional
    public ModelArtifact findCurrent(Integer projectId) {
        return modelArtifactMapper.findByProjectId(projectId);
    }

    @Override
    @Transactional
    public void upload(ModelArtifact modelArtifact) {
        Objects.requireNonNull(modelArtifact.getProjectId());
        if (modelArtifact.getUploadTime() == null) {
            modelArtifact.setUploadTime(new Date());
        }
        ModelArtifact current = modelArtifactMapper.findByProjectId(modelArtifact.getProjectId());
        if (current == null || !StringUtils.equals(modelArtifact.getJsonContentSha256(), current.getJsonContentSha256())) {
            saveJsonFile(modelArtifact);
            modelArtifactMapper.add(modelArtifact);
        } else {
            log.info("The json file is same as before.");
        }
    }

    @Override
    @Transactional
    public ModelArtifact findByVersionId(Integer versionId) {
        ModelArtifact version = modelArtifactMapper.findByVersionId(versionId);
        if (version == null) {
            log.warn("There is model artifact of version: {}", versionId);
        }
        return version;
    }

    @Override
    public void loadContent(ModelArtifact modelArtifact) throws IOException {
        try {
            this.loadFile(modelArtifact);
        } catch (IOException exception) {
            log.warn("Load json file failed", exception);
            throw exception;
        }
    }

    // FIXME: 2023/2/17 to delete
    @Override
    public Object findPlotData(ModelArtifact modelArtifact, String imgId, String imgClass, String imgSrc) {
        /*
        JsonModel jsonModel = modelArtifact.getJsonModel();
        if (jsonModel == null) {
            return null;
        }
        Fairness fairness = jsonModel.getFairness();
        return fairness.getClassDistribution();
         */
        final String  cs_json = "json_test/model_artifact_credit_scoring_20230117_1251.json";
        try (InputStream is = new ClassPathResource(cs_json).getInputStream()) {
            String jsonString = IOUtils.toString(is, StandardCharsets.UTF_8);
            JsonModel jsonModel = objectMapper.readValue(jsonString, JsonModel.class);
            return jsonModel.getFairness().getClassDistribution();
        } catch (IOException e) {
            log.error("load json failed.", e);
            return null;
        }
    }

    @Override
    public void saveJsonFile(ModelArtifact artifact) throws FileSystemException {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(artifact.getProjectId());
        if (StringUtils.isEmpty(artifact.getJsonContent())) {
            throw new IllegalArgumentException();
        }
        if (StringUtils.isEmpty(artifact.getFilename())) {
            throw new IllegalArgumentException();
        }
        String sha256 = DigestUtils.sha256Hex(artifact.getJsonContent());

        File jsonDir = null;
        try {
            jsonDir = veritasProperties.getJsonDirectory(artifact.getProjectId());
        } catch (IOException exception) {
            log.warn("Get the json directory failed.", exception);
            throw new FileSystemException("Save file failed.", exception);
        }

        String zipFilename = sha256 + ".json.zip";
        File zip = new File(jsonDir, zipFilename);
        if (zip.exists()) {
            log.warn("The file[{}] exists.", zip.getPath());
        } else {
            try {
                FileUtils.createParentDirectories(zip);
                File zipTemp = new File(jsonDir, zipFilename + ".tmp");
                try (FileOutputStream fileOutputStream = new FileOutputStream(zipTemp)) {
                    try (ZipOutputStream zos = new ZipOutputStream(fileOutputStream)) {
                        ZipEntry zipEntry = new ZipEntry(artifact.getFilename());
                        zos.putNextEntry(zipEntry);
                        IOUtils.write(artifact.getJsonContent(), zos, StandardCharsets.UTF_8);
                        zos.closeEntry();
                    }
                } catch (IOException ioException) {
                    log.warn("Write file[{}] failed.", zipTemp.getPath(), ioException);
                    throw ioException;
                }
                FileUtils.moveFile(zipTemp, zip);
            } catch (IOException e) {
                log.warn("save failed.", e);
                throw new FileSystemException("Save file failed.", e);
            }
        }
        artifact.setJsonContentSha256(sha256);
        artifact.setJsonZipPath(FilenameUtils.getName(zip.getPath()));
    }

    private void loadFile(ModelArtifact artifact) throws IOException {
        Objects.requireNonNull(artifact);
        Objects.requireNonNull(artifact.getProjectId());
        StringUtils.isEmpty(artifact.getFilename());

//        String zipPath = this.zipPath(artifact.getProjectId(), artifact.getJsonContentSha256());
        String zipPath = artifact.getJsonZipPath();

        File zip = new File(veritasProperties.getJsonDirectory(artifact.getProjectId()), zipPath);
        if (!zip.exists()) {
            throw new FileNotFoundException(zip.getPath());
        }
        try (
                ZipFile zipFile = new ZipFile(zip)) {
            ZipEntry zipEntry = zipFile.getEntry(artifact.getFilename());
            if (zipEntry == null) {
                Enumeration<?> e = zipFile.entries();
                while (e.hasMoreElements()) {
                    zipEntry = (ZipEntry) e.nextElement();
                    if (!zipEntry.isDirectory()) {
                        break;
                    }
                }
            }
            if (zipEntry == null) {
                // FIXME: 2021/9/9
                throw new FileNotFoundException("Not found the file.");
            }
            try (InputStream is = zipFile.getInputStream(zipEntry)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);

                String sha256 = DigestUtils.sha256Hex(content);
                if (!StringUtils.equals(sha256, artifact.getJsonContentSha256())) {
                    // FIXME: 2021/9/9
                    throw new IOException();
                }

                JsonModel jsonModel = parser(content);
                artifact.setJsonContent(content);
                artifact.setJsonModel(jsonModel);

            }
        }


    }
}