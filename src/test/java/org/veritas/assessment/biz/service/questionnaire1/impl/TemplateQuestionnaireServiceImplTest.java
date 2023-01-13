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

package org.veritas.assessment.biz.service.questionnaire1.impl;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire1.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.PermissionException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class TemplateQuestionnaireServiceImplTest {
    @Autowired
    TemplateQuestionnaireService service;

    @Test
    void testCreate_success() {
        assertNotNull(service);
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        assertNotNull(templateQuestionnaire);
    }

    @Test
    void testDelete_success() {
        assertNotNull(service);
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        assertNotNull(templateQuestionnaire);
        service.delete(templateQuestionnaire.getTemplateId());
    }

    @Test
    void testFindList_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");

        List<TemplateQuestionnaire> list = service.findTemplateList();
        assertEquals(2, list.size());
    }

    @Test
    void testEditBasicInfo_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        String newName = "new_name";
        String newDescription = "new description";
        service.updateBasicInfo(templateQuestionnaire.getTemplateId(), newName, newDescription);

        TemplateQuestionnaire after = service.findQuestionnaireById(templateQuestionnaire.getTemplateId());
        assertEquals(newName, after.getName());
        assertEquals(newDescription, after.getDescription());
    }

    @Test
    void testEdit_fail() {
        TemplateQuestionnaire first = service.findQuestionnaireById(1);
        assertThrows(PermissionException.class, () -> {
            service.delete(1);
        });
        assertThrows(PermissionException.class, () -> {
            service.deleteMainQuestion(1, 1);
        });
        assertThrows(PermissionException.class, () -> {
            service.deleteSubQuestion(1, 2);
        });
        assertThrows(PermissionException.class, () -> {
            service.addMainQuestion(1, null);
        });
        assertThrows(PermissionException.class, () -> {
            service.updateMainQuestionWithSub(1, null);
        });
    }

    @Test
    void testDeleteMain_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        int templateId = templateQuestionnaire.getTemplateId();

        TemplateQuestion toDelete = templateQuestionnaire.findMainQuestionWitSubByTitle("E1");
        int result = service.deleteMainQuestion(templateId, toDelete.getId());
        assertEquals(result, toDelete.toList().size());


        TemplateQuestion toAdd = new TemplateQuestion();
        toAdd.setTemplateId(templateId);
        toAdd.setPart("E");
        toAdd.setContent("new Content");
        toAdd.setEditable(false);
    }

    @Test
    void testDeleteSub_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        int templateId = templateQuestionnaire.getTemplateId();

        TemplateQuestion toDeleteMain = templateQuestionnaire.findMainQuestionWitSubByTitle("E1");
        TemplateQuestion toDeleteSub = toDeleteMain.findSub(1);
        int result = service.deleteSubQuestion(templateId, toDeleteSub.getId());
        assertEquals(1, result);
        templateQuestionnaire = service.findQuestionnaireById(templateId);
        TemplateQuestion afterMain = templateQuestionnaire.findMainQuestionWitSubByTitle("E1");
        assertEquals(toDeleteMain.getSubQuestions().size() - 1, afterMain.getSubQuestions().size());
    }

    @Test
    void testAddQuestion_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        int templateId = templateQuestionnaire.getTemplateId();

        TemplateQuestion toAdd = new TemplateQuestion();
        toAdd.setTemplateId(templateId);
        toAdd.setPart("E");
        toAdd.setContent("new Content");
        toAdd.setEditable(false);

        service.addMainQuestion(templateId, toAdd);
    }

    @Test
    void testEditQuestion_success() {
        TemplateQuestionnaire templateQuestionnaire =
                service.create(1, "new template", "For test.");
        int templateId = templateQuestionnaire.getTemplateId();

        TemplateQuestion main = templateQuestionnaire.findMainQuestionWitSubByTitle("E1");
        for (TemplateQuestion question : main.toList()) {
            question.setContent("content");
            question.setHint("hint");
        }

        TemplateQuestion after = service.updateMainQuestionWithSub(templateId, main);
        log.info("after edit: {}", after);

    }
}