package org.veritas.assessment.biz.mapper.questionnaire;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
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

    @Test
    @Sql(statements = "insert into vat2_question_meta (id, project_id, main_question_id, current_vid, add_start_questionnaire_vid)" +
            " values (99999, 8, 100, 200, 300)")
    void updateVersionId() {
        long questionId = 99999L;
        long oldVid = 200L;
        long newVid = 900L;
        boolean result = mapper.updateVersionId(questionId, oldVid, newVid);
        assertEquals(true, result);

        long fakeOldVid = 1000L;
        long newVid2 = 12000L;
        result = mapper.updateVersionId(questionId, fakeOldVid, newVid2);
        assertEquals(false, result);
    }
}