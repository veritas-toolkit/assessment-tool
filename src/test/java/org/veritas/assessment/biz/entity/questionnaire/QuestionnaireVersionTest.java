package org.veritas.assessment.biz.entity.questionnaire;

import org.junit.jupiter.api.Test;
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.biz.service.IdGenerateService;
import org.veritas.assessment.biz.service.impl.SnowFlakeServiceImpl;

import static org.junit.jupiter.api.Assertions.*;

class QuestionnaireVersionTest {

    IdGenerateService idGenerateService = new SnowFlakeServiceImpl();

    private QuestionnaireVersion testData() {
        QuestionnaireVersion questionnaire = new QuestionnaireVersion();
        Long qvid = idGenerateService.nextId();
        questionnaire.setVid(qvid);
        questionnaire.setMessage("created.");

        for (Principle principle : Principle.values()) {
            int serial = 0;
            for (AssessmentStep step : AssessmentStep.values()) {
                QuestionNode questionNode = new QuestionNode();

            }
        }



        return questionnaire;
    }

}