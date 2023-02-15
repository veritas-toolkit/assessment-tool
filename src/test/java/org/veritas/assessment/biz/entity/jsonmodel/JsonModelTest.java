package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class JsonModelTest {
    public static final String EXAMPLE_CS = "json2/model_artifact_credit_scoring_20230117_1251.json";
    public static final String EXAMPLE_CM = "json2/model_artifact_customer_marketing_20230117_1143.json";


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


    @Test
    void testLoad_successCreditScore() throws IOException {
        JsonModel jsonModel = load(EXAMPLE_CS);
        Transparency transparency = jsonModel.getTransparency();
        assertNotNull(transparency);
    }

    @Test
    void testLoad_successCustomerMarketing() throws IOException {
        JsonModel jsonModel = load(EXAMPLE_CM);
        Transparency transparency = jsonModel.getTransparency();
        assertNotNull(transparency);
    }
}