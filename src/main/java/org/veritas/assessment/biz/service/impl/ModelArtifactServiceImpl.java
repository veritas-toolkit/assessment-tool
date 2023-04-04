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
import org.veritas.assessment.biz.constant.PlotTypeEnum;
import org.veritas.assessment.biz.dto.PlotDataDto;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.Fairness;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.biz.entity.jsonmodel.Transparency;
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
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
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
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        if (veritasProperties.isTestProfileActive()) {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        } else {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);
        }
    }

    @Override
    public JsonModel parser(String json) {
        try {
            return objectMapper.readValue(json, JsonModel.class);
        } catch (Exception exception) {
            log.warn("Cannot parse the json file.", exception);
            throw new ErrorParamException("Server cannot parse the json file. Please check your file.");
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

//    @Cacheable(cacheNames = "model_artifact",key = "'vid_'+#modelArtifact.versionId")
    @Override
    public void loadContent(ModelArtifact modelArtifact) throws IOException {
        try {
            this.loadFile(modelArtifact);
        } catch (IOException exception) {
            log.warn("Load json file failed", exception);
            throw exception;
        }
    }

    @Override
    public PlotDataDto findPlotData(ModelArtifact modelArtifact, String imgId, String imgClass, String imgSrc) {
        PlotDataDto dto = new PlotDataDto();
        try {
            JsonModel jsonModel = modelArtifact.getJsonModel();
            if (jsonModel == null) {
                this.loadContent(modelArtifact);
            }
            if (jsonModel == null) {
                return PlotDataDto.none();
            }
            if (jsonModel.getFairness() != null) {
                final String calibrationCurveLineChart = "calibrationCurveLineChart";
                if (StringUtils.contains(imgSrc, calibrationCurveLineChart)) {
                    dto.setData(jsonModel.getFairness().getCalibrationCurve());
                    dto.setType(PlotTypeEnum.CURVE);
                    dto.setName("Calibration Curve");
                    dto.setCaption("CalibrationCurve");
                    return dto;
                }
                final String classDistributionPieChart = "classDistributionPieChart";
                if (StringUtils.contains(imgSrc, classDistributionPieChart)) {
                    dto.setData(jsonModel.getFairness().getClassDistribution());
                    dto.setType(PlotTypeEnum.PIE);
                    dto.setName("Class Distribution");
                    dto.setCaption("Class Distribution");
                    return dto;
                }
                final String correlationHeatMapChart = "correlationHeatMapChart";
                if (StringUtils.contains(imgSrc, correlationHeatMapChart)) {
                    dto.setData(jsonModel.getFairness().getCorrelationMatrix());
                    dto.setType(PlotTypeEnum.CORRELATION_MATRIX);
                    dto.setName("CorrelationHeat");
                    dto.setCaption("CorrelationHeat");
                    return dto;
                }
                final String weightedConfusionHeatMapChart = "weightedConfusionHeatMapChart";
                if (StringUtils.contains(imgSrc, weightedConfusionHeatMapChart)) {
                    dto.setData(jsonModel.getFairness().getCorrelationMatrix());
                    dto.setType(PlotTypeEnum.CONFUSION_MATRIX);
                    dto.setName("Weighted Confusion Heatmap");
                    dto.setCaption("weightedConfusionHeatMapChart");
                    return dto;
                }


                final String featureDistributionPieChartMap = "featureDistributionPieChartMap";
                if (StringUtils.contains(imgSrc, featureDistributionPieChartMap)) {
                    Map<String, Fairness.Feature> featureMap = jsonModel.getFairness().getFeatureMap();
                    for (String name : featureMap.keySet()) {
                        if (StringUtils.contains(imgSrc, featureDistributionPieChartMap + "_" + name + ".")) {
                            dto.setData(featureMap.get(name).getFeatureDistributionMap());
                            dto.setType(PlotTypeEnum.PIE);
                            dto.setName("Feature Distribution Pie Chart");
                            dto.setCaption("Feature Distribution Pie Chart");
                            return dto;
                        }
                    }
                }


                final String performanceLineChart = "performanceLineChart";
                if (StringUtils.contains(imgSrc, performanceLineChart)) {
                    dto.setData(jsonModel.getFairness().getPerfDynamic());
                    dto.setType(PlotTypeEnum.TWO_LINE);
                    dto.setName("Performance Line Chart");
                    dto.setCaption("Performance Line Chart");
                    return dto;
                }
            }
            if (jsonModel.getTransparency() != null) {
                Transparency transparency = jsonModel.getTransparency();
                final String permutationImportance = "permutationImportance";
                if (StringUtils.contains(imgSrc, permutationImportance)) {
                    Transparency.Permutation permutation = transparency.getPermutation();
                    if (permutation == null || permutation.getScore() == null) {
                        return PlotDataDto.none();
                    }
                    List<Transparency.PermutationScore> scoreList = permutation.getScore();
                    scoreList = scoreList.stream()
                            .sorted((a, b) -> {
                                if (a.getScore() == null) {
                                    return -1;
                                } else if (b.getScore() == null) {
                                    return 1;
                                } else {
                                    return a.getScore().compareTo(b.getScore());
                                }
                            })
                            .collect(Collectors.toList());
                    dto.setData(scoreList);
                    dto.setType(PlotTypeEnum.H_BAR);
                    if (StringUtils.isEmpty(permutation.getTitle())) {
                        dto.setName("Permutation Importance");
                    } else {
                        dto.setName(permutation.getTitle());
                    }
                    dto.setCaption("Permutation Importance");
                    return dto;
                }

                final String waterfall = "waterfall";
                if (StringUtils.contains(imgSrc, waterfall)) {
                    List<Transparency.ModelInfo> modelList = transparency.getModelList();
                    if (modelList != null && !modelList.isEmpty()) {
                        for (Transparency.ModelInfo modelInfo : modelList) {
                            Integer mi = modelInfo.getId();
                            List<Transparency.LocalInterpretability> localInterpretabilityList =
                                    modelInfo.getLocalInterpretabilityList();
                            if (localInterpretabilityList != null) {
                                for (Transparency.LocalInterpretability localInterpretability :
                                        localInterpretabilityList) {
                                    Integer li = localInterpretability.getId();
                                    String partialName = String.format("%s_%d_%d", waterfall, mi, li);
                                    if (StringUtils.contains(imgSrc, partialName)) {
                                        dto.setData(localInterpretability);
                                        dto.setType(PlotTypeEnum.WATERFALL);
                                        dto.setName("Local Interpretability");
                                        dto.setCaption("Local Interpretability");
                                        return dto;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException | NullPointerException exception) {
            return PlotDataDto.none();
        }
        return PlotDataDto.none();
    }

    public static JsonModel load(String urlString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objectMapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
//        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        URL url = new ClassPathResource(urlString).getURL();
        return objectMapper.readValue(url, JsonModel.class);
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
        if (log.isDebugEnabled()) {
            log.debug("read the json from zip: {}", zip.getAbsolutePath());
        }
        try (ZipFile zipFile = new ZipFile(zip)) {
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
                throw new FileNotFoundException("Not found the file.");
            }
            try (InputStream is = zipFile.getInputStream(zipEntry)) {
                String content = IOUtils.toString(is, StandardCharsets.UTF_8);

//                String sha256 = DigestUtils.sha256Hex(content);
//                if (!StringUtils.equals(sha256, artifact.getJsonContentSha256())) {
                    // FIXME: 2021/9/9
//                    throw new IOException();
//                }

                JsonModel jsonModel = parser(content);
                artifact.setJsonContent(content);
                artifact.setJsonModel(jsonModel);

            }
        }
        if (log.isDebugEnabled()) {
            log.info("read done");
        }
    }
}