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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.biz.entity.jsonmodel.Transparency;
import org.veritas.assessment.biz.service.GraphService;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class GraphServiceImpl implements GraphService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private VeritasProperties veritasProperties;
    @Override
    public GraphContainer createAllGraph(ModelArtifact modelArtifact) {
        try {
            File imageDir = veritasProperties.getImageDirFile(modelArtifact.getProjectId());
        } catch (IOException exception) {
            throw new RuntimeException("create image dir failed.", exception);
        }
        GraphContainer container = createAllGraphByPython(modelArtifact);

        try {
            savePlot(modelArtifact, container);
        } catch (IOException exception) {
            throw new IllegalStateException("save failed.", exception);
        }
        return container;

    }

    private void savePlot(ModelArtifact modelArtifact, GraphContainer container) throws IOException  {
        JsonModel jsonModel = modelArtifact.getJsonModel();
        if (jsonModel == null) {
            return;
        }
        Transparency transparency = jsonModel.getTransparency();
        if (transparency == null) {
            return;
        }
        List<Transparency.ModelInfo> modelList = transparency.getModelList();
        if (modelList == null || modelList.isEmpty()) {
            return;
        }
        File imageDir = veritasProperties.getImageDirFile(modelArtifact.getProjectId());
        String prefix = String.format("api/project/%d/image", modelArtifact.getProjectId());
        for (Transparency.ModelInfo modelInfo : modelList) {
            if (modelInfo.hasSummaryPlotBase64()) {
                // summary plot
                String summaryPlotFilename = String.format("%s_summaryPlot_%d.png",
                        modelArtifact.getJsonContentSha256(), modelInfo.getId());
                this.saveFile(imageDir, summaryPlotFilename ,modelInfo.getSummaryPlotImage());
                ImageEnum.SUMMARY_PLOT.put(container, prefix, summaryPlotFilename);
            }
            for (Map.Entry<String, byte[]> entry : modelInfo.partialDependencePlotMap().entrySet()) {
                String feature = entry.getKey();
                byte[] content = entry.getValue();
                if (content != null && content.length > 0) {
                    String filename = String.format("%s_partialDependencePlot_%d_%s.png",
                            modelArtifact.getJsonContentSha256(), modelInfo.getId(), feature);
                    this.saveFile(imageDir, filename, content);
                    ImageEnum.PARTIAL_DEPENDENCE_PLOT.put(container, prefix, filename);
                }
            }
        }
    }

    private void saveFile(File dir, String filename, byte[] content) throws IOException {
        Objects.requireNonNull(dir);
        Objects.requireNonNull(filename);
        Objects.requireNonNull(content);
        if (!dir.exists()) {
            FileUtils.createParentDirectories(dir);
            boolean ret = dir.mkdirs();
            if (!ret) {
                throw new IOException("Save plot failed.");
            }
        }
        File file = new File(dir, filename);
        try (OutputStream outputStream = Files.newOutputStream(file.toPath())) {
            IOUtils.write(content, outputStream);
        }
    }



    private GraphContainer createAllGraphByPython(ModelArtifact modelArtifact) {
        Objects.requireNonNull(modelArtifact);
        Objects.requireNonNull(modelArtifact.getProjectId());
        Objects.requireNonNull(modelArtifact.getJsonZipPath());

        GraphContainer container = new GraphContainer();

        String prefix = String.format("api/project/%d/image", modelArtifact.getProjectId());

        List<String> filePathList = Collections.emptyList();
        try {
            File json = new File(veritasProperties.getJsonDirectory(modelArtifact.getProjectId()), modelArtifact.getJsonZipPath());
            filePathList = callPython(json.getAbsolutePath());
            log.info("filename list: {}", filePathList);
        } catch (Exception exception) {
            log.warn("exception", exception);
        }
        if (filePathList == null || filePathList.isEmpty()) {
            log.warn("Python plot failed.");
            filePathList = Collections.emptyList();
        }

        for (String filePath : filePathList) {
            String filename = FilenameUtils.getName(filePath);
            for (ImageEnum imageEnum : ImageEnum.values()) {
                if (imageEnum.is(filename)) {
                    String url = imageEnum.put(container, prefix, filename);
                    if (log.isDebugEnabled()) {
                        log.debug("image url [{}]: {}", imageEnum.name, url);
                    }
                }
            }
        }
        return container;
    }

    private List<String> callPython(String filePath) {

        try {
            log.warn("filePath: {}", filePath);
            ProcessBuilder builder = new ProcessBuilder(
                    veritasProperties.getPythonCommand(),
                    "py/plot.py",
                    filePath);
            builder.redirectErrorStream(false);
            StopWatch stopWatch = StopWatch.createStarted();
            Process process = builder.start();
            stopWatch.stop();
            log.info("process time: {}", stopWatch);

            String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
            log.info("stdout:\n{}", result);
            String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            stderr = StringUtils.trimToNull(stderr);
            if (stderr != null) {
                log.warn("stderr:\n{}", stderr);
                if (veritasProperties.isTestProfileActive()) {
                    throw new IllegalStateException("Call python with stderr output.");
                }
            }
            if (process.exitValue() != 0) {
                log.error("plot exit with non-zero code.");
                if (veritasProperties.isTestProfileActive()) {
                    throw new IllegalStateException("call python failed.");
                }
            }
            if (!StringUtils.isEmpty(result)) {
                return objectMapper.readValue(result,
                        new TypeReference<List<String>>() {
                        });
            }
        } catch (Exception exception) {
            if (veritasProperties.isTestProfileActive()) {
                throw new RuntimeException("Call python command failed.", exception);
            } else {
                log.error("Failed to call python plot.py", exception);
                throw new IllegalRequestException("Failed to parse json file");
            }

        }
        return Collections.emptyList();
    }

    private enum ImageEnum {
        calibrationCurveLineChart("calibrationCurveLineChart", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (calibrationCurveLineChart.is(imageFilename)) {
                    String url = urlPrefix + "/" + imageFilename;
                    container.setCalibrationCurveLineChart(url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        classDistributionPieChart("classDistributionPieChart", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (classDistributionPieChart.is(imageFilename)) {
                    String url = urlPrefix + "/" + imageFilename;
                    container.setClassDistributionPieChart(url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        correlationHeatMapChart("correlationHeatMapChart", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (correlationHeatMapChart.is(imageFilename)) {
                    String url = urlPrefix + "/" + imageFilename;
                    container.setCorrelationHeatMapChart(url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        featureDistributionPieChartMap("featureDistributionPieChartMap", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (featureDistributionPieChartMap.is(imageFilename)) {
                    String basename = FilenameUtils.getBaseName(imageFilename);
                    String[] sectionArray = StringUtils.split(basename, "_");
                    String key = "default";
                    if (sectionArray.length >= 3) {
                        key = sectionArray[2];
                    }
                    String url = urlPrefix + "/" + imageFilename;
                    container.putFeatureDistributionPieChart(key, url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        featureTradeoffContourMap("featureTradeoffContourMap", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (featureTradeoffContourMap.is(imageFilename)) {
                    String basename = FilenameUtils.getBaseName(imageFilename);
                    String[] sectionArray = StringUtils.split(basename, "_");
                    String key = "default";
                    if (sectionArray.length >= 3) {
                        key = sectionArray[2];
                    }
                    String url = urlPrefix + "/" + imageFilename;
                    container.putFeatureTradeoffContour(key, url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        performanceLineChart("performanceLineChart", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (performanceLineChart.is(imageFilename)) {
                    String url = urlPrefix + "/" + imageFilename;
                    container.setPerformanceLineChart(url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        weightedConfusionHeatMapChart("weightedConfusionHeatMapChart", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (weightedConfusionHeatMapChart.is(imageFilename)) {
                    String url = urlPrefix + "/" + imageFilename;
                    container.setWeightedConfusionHeatMapChart(url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        SUMMARY_PLOT("summaryPlot", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (SUMMARY_PLOT.is(imageFilename)) {
                    String basename = FilenameUtils.getBaseName(imageFilename);
                    String[] sectionArray = StringUtils.split(basename, "_");
                    String key = "default";
                    if (sectionArray.length >= 3) {
                        key = sectionArray[2];
                    }
                    String url = urlPrefix + "/" + imageFilename;
                    container.putSummaryPlot(key, url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        PARTIAL_DEPENDENCE_PLOT("partialDependencePlot", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                if (PARTIAL_DEPENDENCE_PLOT.is(imageFilename)) {
                    // {sha256}_partialDependencePlot_{id}_{feature}
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
                    String url = urlPrefix + "/" + imageFilename;
                    container.putPartialDependencePlot(key, feature, url);
                    return url;
                } else {
                    throw new IllegalStateException();
                }
            }
        }),
        WATER_FALL_PLOT("waterfall", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                String url = urlPrefix + "/" + imageFilename;
                container.putWaterfall(url);
                return url;
            }
        }),
        PERMUTATION_IMPORTANCE_PLOT("permutationImportance", new PutFunction() {
            @Override
            public String put(GraphContainer container, String urlPrefix, String imageFilename) {
                String url = urlPrefix + "/" + imageFilename;
                container.setPermutationImportancePlot(url);
                return url;
            }
        })
        ;

        private final String name;

        private final PutFunction putFunction;

        ImageEnum(String name, PutFunction putFunction) {
            this.name = name;
            this.putFunction = putFunction;
        }

        public boolean is(String filename) {
            return StringUtils.containsIgnoreCase(filename, this.name);
        }

        String put(GraphContainer container, String urlPrefix, String imageFilename) {
            return putFunction.put(container, urlPrefix, imageFilename);
        }
    }

    @FunctionalInterface
    public interface PutFunction {
        String put(GraphContainer container, String urlPrefix, String imageFilename);
    }


}
