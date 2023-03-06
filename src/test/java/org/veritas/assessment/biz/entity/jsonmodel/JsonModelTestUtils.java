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

    public static final String EXAMPLE_CS = "json2/model_artifact_credit_scoring_20230306_1054.json";

    public static final String EXAMPLE_CM = "json2/model_artifact_custmr_marketing_20230306_1059.json";
    @Deprecated
    public static final String EXAMPLE_PUW = "json2/model_artifact_pred_underwriting_v2.json";


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