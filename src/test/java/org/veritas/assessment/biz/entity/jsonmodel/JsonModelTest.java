package org.veritas.assessment.biz.entity.jsonmodel;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.veritas.assessment.TestUtils;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class JsonModelTest {


    @Test
    void testLoad_successCreditScore() throws IOException {
        JsonModel jsonModel = JsonModelTestUtils.load(JsonModelTestUtils.EXAMPLE_CS);
        Transparency transparency = jsonModel.getTransparency();
        assertNotNull(transparency);
        log.info("summary plot data: {} ", transparency.getModelList().get(0).getSummaryPlotFeatureList());
    }

    @Test
    void testLoad_successCustomerMarketing() throws IOException {
        JsonModel jsonModel = JsonModelTestUtils.load(JsonModelTestUtils.EXAMPLE_CM);
        Transparency transparency = jsonModel.getTransparency();
        assertNotNull(transparency);
        log.info("transparency:\n{}", TestUtils.toJson(transparency));
    }
}