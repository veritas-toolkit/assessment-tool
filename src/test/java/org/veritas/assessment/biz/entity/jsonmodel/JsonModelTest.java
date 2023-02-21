package org.veritas.assessment.biz.entity.jsonmodel;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.veritas.assessment.TestUtils;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JsonModelTest {

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


//    @Test
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
        log.info("transparency:\n{}", TestUtils.toJson(transparency));
    }
}