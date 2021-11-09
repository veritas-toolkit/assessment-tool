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

package org.veritas.assessment.biz.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JsonModelTest {

    public static final String EXAMPLE_URL = "json/veritas_model_artifact_final_V1.1.json";
    public static final String rejectionUrl = "json/model_artifact_cm_rejection_20210903_1629.pretty.json";
    //    public static final String creditScoringUrl  = "json/model_artifact_credit_scoring_20210903_1617.pretty.json";
    public static final String creditScoringUrl = "json/model_artifact_credit_scoring_20210928_1652.pretty.json";
    public static final String DEFAULT_JSON_URL = "json/model_artifact_default_20210906_1417.pretty.json";
    public static final String DEFAULT_FIX_JSON_URL = "json/model_artifact_default_20210906_1417.pretty.fix.json";

    public static final String[] urlStringArray = {
            rejectionUrl,
            creditScoringUrl,
            DEFAULT_JSON_URL
    };
    private ObjectMapper objectMapper;

    public static String loadJson(String jsonUrl) throws IOException {
        try (InputStream inputStream = new ClassPathResource(jsonUrl).getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("", exception);
        }
    }

    public static JsonModel load(String urlString) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        URL url = new ClassPathResource(urlString).getURL();
        JsonModel jsonModel = objectMapper.readValue(url, JsonModel.class);
        return jsonModel;
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.enable(JsonParser.Feature.ALLOW_YAML_COMMENTS);
        objectMapper.enable(JsonParser.Feature.ALLOW_COMMENTS);
//        objectMapper.enable(JsonParser.Feature.IGNORE_UNDEFINED);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

    }

    @Test
    void testLoad() throws IOException {
        for (String url : urlStringArray) {
            JsonModel jsonModel = load(url);
            assertNotNull(jsonModel);
        }
    }

    @Test
    void name() throws Exception {
//        URL url = new ClassPathResource("json/veritas_model_artifact_final.json").getURL();
//        URL url = new ClassPathResource("json/veritas_model_artifact_final_2.json").getURL();
        URL url = new ClassPathResource("json/veritas_model_artifact_final_V1.1_no_comment.json").getURL();
        JsonModel jsonModel = objectMapper.readValue(url, JsonModel.class);
        log.info("jsonModel: {}", jsonModel);
        log.info("equal_odds: {}", jsonModel.getFeatureMap().get("gender").getFairMetricValueListMap().get("equal_odds").get(0));

//        Map<String, Map<String, List<BigDecimal>>> specialParameterMap = jsonModel.getFairnessInit().getSpecialParameters();
        Map<String, Object> specialParameterMap = jsonModel.getFairnessInit().getSpecialParameters();
        log.info("special_params: {}", specialParameterMap);
        assertNotNull(specialParameterMap);
//        assertEquals(new BigDecimal("80"), specialParameterMap.get("num_applicants").get("gender").get(0));

        JsonModel.WeightedConfusionMatrix weightedConfusionMatrix = jsonModel.getWeightedConfusionMatrix();
        log.info("weightedConfusionMatrix: {}", weightedConfusionMatrix);
        assertEquals(new BigDecimal("1335.6"), weightedConfusionMatrix.getTp());

        // /perf_dynamic/perf_metric_name
        JsonModel.PerfDynamic perfDynamic = jsonModel.getPerfDynamic();
        log.info("perfDynamic: {}", perfDynamic);
        assertNotNull(perfDynamic);
        assertEquals("balanced_accuracy", perfDynamic.getPerfMetricName());

    }

    @Test
    void testLoadExampleFile() throws IOException {
        for (String urlString : urlStringArray) {
            URL url = new ClassPathResource(urlString).getURL();
            byte[] bytes = IOUtils.toByteArray(url);
            StopWatch stopWatch = StopWatch.createStarted();
            JsonModel jsonModel = objectMapper.readValue(bytes, JsonModel.class);
            assertNotNull(jsonModel);
            stopWatch.stop();
            log.info("=============================================");
            log.info("Time: {}", stopWatch.formatTime());
            log.info("perf metric name: {}", jsonModel.getFairnessInit().getPerfMetricName());
            log.info("fair metric name: {}", jsonModel.getFairnessInit().getFairMetricName());
        }
        log.info("=============================================");
    }

    @Test
    void testLoad_success2() throws Exception {
//        URL url = new ClassPathResource("json/model_artifact_credit scoring_20210901_1752.json").getURL();
//        URL url = new ClassPathResource("json/model_artifact_cm_rejection_20210901_1759.json").getURL();

//        URL url = new ClassPathResource("json/model_artifact_credit_scoring_20210901_1752_pretty.json").getURL();
//        URL url = new ClassPathResource("json/model_artifact_cm_rejection_20210901_1759_pretty.json").getURL();

        URL url = new ClassPathResource("json/model_artifact_cm_rejection_20210903_1629.pretty.json").getURL();
//        URL url = new ClassPathResource("json/model_artifact_credit_scoring_20210903_1617.pretty.json").getURL();


        StopWatch stopWatch = StopWatch.createStarted();
        JsonModel jsonModel = objectMapper.readValue(url, JsonModel.class);
        assertNotNull(jsonModel);
        stopWatch.stop();
        log.info("Time: {}", stopWatch.formatTime());
//        log.info("Time: {}", stopWatch.formatSplitTime());
//        log.info("jsonModel: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonModel));

    }

    @Test
    void testFeatureIsLow() throws IOException {
        JsonModel jsonModel = load(DEFAULT_JSON_URL);

        jsonModel.getFeatureMap().forEach((key, feature) -> {
            log.info("is low: {}", feature.isLow());
            assertTrue(feature.isLow());
        });
    }

    @Test
    void testFairnessInit() throws IOException {
        JsonModel jsonModel = load(creditScoringUrl);

        jsonModel.getFeatureMap().forEach((key, feature) -> {
            log.info("is low: {}", feature.isLow());
            assertTrue(feature.isLow());
        });

        JsonModel.FairnessInit fairnessInit = jsonModel.getFairnessInit();
        log.info("fairness init:\n{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fairnessInit));
        assertEquals("auto", fairnessInit.getFairMetricNameInput());
        assertEquals("harm", fairnessInit.getFairPriority());
        assertEquals("inclusive", fairnessInit.getFairConcern());
        assertEquals("normal", fairnessInit.getFairImpact());
        assertEquals(new BigDecimal("80"), fairnessInit.getFairThresholdInput());
        assertEquals(new BigDecimal("0.001"), fairnessInit.getFairNeutralTolerance());
    }

    @Test
    void testFeatureFaireMetricValueFormat() {
        List<BigDecimal> list = null;
        String output = null;
        output = JsonModel.Feature.faireMetricValueFormat(list);
        log.info("input: {}", "null");
        log.info("output: {}", output);
        assertEquals("0.000 +/- 0.000", output);

        list = Arrays.asList(BigDecimal.valueOf(0.916), BigDecimal.valueOf(0.222));
        output = JsonModel.Feature.faireMetricValueFormat(list);
        log.info("input: {}", list);
        log.info("output: {}", output);
        assertEquals("0.916 +/- 0.222", output);

        list = Arrays.asList(new BigDecimal("0.9"), new BigDecimal("0.123556675345"));
        output = JsonModel.Feature.faireMetricValueFormat(list);
        log.info("input: {}", list);
        log.info("output: {}", output);
        assertEquals("0.900 +/- 0.124", output);

        list = Arrays.asList(new BigDecimal("-0.38"), new BigDecimal("0.123556675345"));
        output = JsonModel.Feature.faireMetricValueFormat(list);
        log.info("input: {}", list);
        log.info("output: {}", output);
        assertEquals("-0.380 +/- 0.124", output);
    }
}