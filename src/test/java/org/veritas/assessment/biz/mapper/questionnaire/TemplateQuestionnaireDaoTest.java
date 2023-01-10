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

package org.veritas.assessment.biz.mapper.questionnaire;

import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Slf4j
@ActiveProfiles("test")
@Transactional
class TemplateQuestionnaireDaoTest {
    @Autowired
    private TemplateQuestionnaireDao dao;

    @Autowired
    private ObjectWriter objectWriter;

    @Test
    void testFindQuestionnaire_success() throws IOException {
        assertNotNull(dao);
        TemplateQuestionnaire questionnaire = dao.findQuestionnaire(1);
        log.info("json:\n{}", objectWriter.writeValueAsString(questionnaire));
    }

    @Test
    void testFindTemplatePageable() {
        Pageable<TemplateQuestionnaire> pageable = dao.findTemplatePageable(null, "au", 1, 20);
        assertEquals(1, pageable.getRecords().size());
        assertEquals("default", pageable.getRecords().get(0).getName());
    }
}