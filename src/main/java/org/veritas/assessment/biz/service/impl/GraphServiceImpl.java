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
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.veritas.assessment.biz.entity.GraphContainer;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;
import org.veritas.assessment.biz.service.GraphService;
import org.veritas.assessment.system.config.VeritasProperties;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class GraphServiceImpl implements GraphService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private VeritasProperties veritasProperties;

    @Override
    public GraphContainer createAllGraph(ModelArtifact modelArtifact) {
        Objects.requireNonNull(modelArtifact);
        Objects.requireNonNull(modelArtifact.getProjectId());
        Objects.requireNonNull(modelArtifact.getJsonZipPath());

        GraphContainer container = new GraphContainer();

        List<String> filePathList = Collections.emptyList();
        try {
            veritasProperties.getImageDirFile(modelArtifact.getProjectId());
            File image = new File(veritasProperties.getJsonDirectory(modelArtifact.getProjectId()), modelArtifact.getJsonZipPath());
            filePathList = callPython(image.getAbsolutePath());
            log.info("filename list: {}", filePathList);
        } catch (Exception exception) {
            log.warn("exception", exception);
        }
        String prefix = String.format("api/project/%d/image", modelArtifact.getProjectId());
        assert filePathList != null;
        for (String filePath : filePathList) {
            String filename = FilenameUtils.getName(filePath);
            for (ImageEnum imageEnum : ImageEnum.values()) {
                if (imageEnum.is(filename)) {
                    String url = imageEnum.put(container, prefix, filename);
                    log.debug("image url [{}]: {}", imageEnum.name, url);
                }

            }
        }
        return container;
    }

    private List<String> callPython(String filePath) throws Exception {

        log.warn("filePath: {}", filePath);
        ProcessBuilder builder = new ProcessBuilder(
                veritasProperties.getPythonCommand(),
                "py/plot.py",
                filePath);
        builder.redirectErrorStream(false);
        StopWatch stopWatch = StopWatch.createStarted();
        Process process = builder.start();

        String result = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        log.info("stdout:\n{}", result);
        String stderr = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
        log.warn("stderr:\n{}", stderr);
        stopWatch.stop();
        log.info("process time: {}", stopWatch);
        if (!StringUtils.isEmpty(result)) {
            return objectMapper.readValue(result,
                    new TypeReference<List<String>>() {
                    });
        }
        return null;
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
