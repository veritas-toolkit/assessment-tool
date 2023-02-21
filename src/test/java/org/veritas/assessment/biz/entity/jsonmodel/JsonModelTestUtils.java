package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class JsonModelTestUtils {

    @Deprecated
    public static final String EXAMPLE_URL = "json/veritas_model_artifact_final_V1.1.json";
    @Deprecated
    public static final String rejectionUrl = "json/model_artifact_cm_rejection_20210903_1629.pretty.json";
    //    public static final String creditScoringUrl  = "json/model_artifact_credit_scoring_20210903_1617.pretty.json";
    @Deprecated
    public static final String creditScoringUrl = "json/model_artifact_credit_scoring_20210928_1652.pretty.json";
    @Deprecated
    public static final String DEFAULT_JSON_URL = "json/model_artifact_default_20210906_1417.pretty.json";
    @Deprecated
    public static final String DEFAULT_FIX_JSON_URL = "json/model_artifact_default_20210906_1417.pretty.fix.json";

    public static final String[] urlStringArray = {
            rejectionUrl,
            creditScoringUrl,
            DEFAULT_JSON_URL
    };
    @Deprecated
    public static final String EXAMPLE_CS = "json2/model_artifact_credit_scoring_20230117_1251.json";
    public static final String EXAMPLE_CM = "json2/model_artifact_customer_marketing_20230220_1849.json";


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

    public static String loadJson(String jsonUrl) throws IOException {
        try (InputStream inputStream = new ClassPathResource(jsonUrl).getInputStream()) {
            return IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new RuntimeException("", exception);
        }
    }

}