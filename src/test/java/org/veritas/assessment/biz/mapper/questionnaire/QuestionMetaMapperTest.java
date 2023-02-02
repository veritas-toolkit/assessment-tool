package org.veritas.assessment.biz.mapper.questionnaire;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.QuestionMeta;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class QuestionMetaMapperTest {

    @Autowired
    private QuestionMetaMapper mapper;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void name() {
        assertNotNull(mapper);
    }

    @Test
    void testAddMainQuestionWithSubs() throws JsonProcessingException {
        final int PROJECT_ID = RandomUtils.nextInt(200, 2000);
        final long Q_VID = RandomUtils.nextInt(2000, 20000);
        QuestionMeta main = new QuestionMeta();
        main.setAddStartQuestionnaireVid(Q_VID);
        main.setProjectId(PROJECT_ID);
        List<QuestionMeta> subList = new ArrayList<>();
        final int SUB_COUNT = 5;
        for (int i = 0; i < SUB_COUNT; ++i) {
            QuestionMeta sub = new QuestionMeta();
            sub.setProjectId(PROJECT_ID);
            sub.setAddStartQuestionnaireVid(Q_VID);
            subList.add(sub);
        }

        log.info("before: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(main));
        int result = mapper.addMainQuestionWithSubs(main);
        log.info("after: {}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(main));
        assertEquals(SUB_COUNT + 1, result);
        assertNotNull(main.getId());
        assertNotNull(main.getMainQuestionId());
    }
}