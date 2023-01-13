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
import org.apache.commons.lang3.SerializationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.questionnaire1.TemplateQuestionnaireDao;
import org.veritas.assessment.biz.service.questionnaire1.AbstractQuestionnaireService;
import org.veritas.assessment.biz.service.questionnaire1.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.PermissionException;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class TemplateQuestionnaireServiceImpl
        extends AbstractQuestionnaireService<TemplateQuestion, TemplateQuestionnaire, TemplateQuestionnaireDao>
        implements TemplateQuestionnaireService {

    @Autowired
    private TemplateQuestionnaireDao dao;

    @Override
    @Transactional
    public void delete(Integer templateId) {
        validCanEdit(templateId);
        int result = dao.deleteById(templateId);
        if (result <= 0) {
            log.warn("delete questionnaire template failed, id:{}", templateId);
        }
    }

    @Override
    @Transactional
    public List<TemplateQuestionnaire> findTemplateList() {
        return dao.findTemplateList();
    }

    @Override
    @Transactional
    public Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword, int page, int pageSize) {
        return dao.findTemplatePageable(prefix, keyword, page, pageSize);
    }

    @Override
    @Transactional
    public TemplateQuestionnaire create(Integer fromTemplateId, String name, String description) {
        TemplateQuestionnaire origin = dao.findQuestionnaire(fromTemplateId);
        TemplateQuestionnaire questionnaire = SerializationUtils.clone(origin);
        questionnaire.setName(name);
        questionnaire.setDescription(description);
        questionnaire.setType(QuestionnaireTemplateType.USER_DEFINED);
        for (TemplateQuestion question : questionnaire.allQuestionsWithSub()) {
            question.setId(null);
            question.setTemplateId(null);
        }
        dao.add(questionnaire);
        log.info("origin :        {}", origin);
        log.info("questionnaire : {}", questionnaire);
        return questionnaire;
    }

    @Override
    @Transactional
    public TemplateQuestionnaire updateBasicInfo(Integer templateId, String name, String description) {
        validCanEdit(templateId);
        dao.updateBasicInfo(templateId, name, description);
        return dao.findQuestionnaire(templateId);
    }

    /**
     * Use {@link TemplateQuestionnaireServiceImpl#create(java.lang.Integer, java.lang.String, java.lang.String)}
     * instead.
     */
    @Override
    @Transactional
    public TemplateQuestionnaire add(TemplateQuestionnaire questionnaire) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Transactional
    public TemplateQuestion addMainQuestion(Integer questionnaireId, TemplateQuestion mainQuestion) {
        validCanEdit(questionnaireId);
        return super.addMainQuestion(questionnaireId, mainQuestion);
    }

    @Override
    @Transactional
    public int deleteSubQuestion(Integer questionnaireId, Integer subQuestionId) {
        validCanEdit(questionnaireId);
        return super.deleteSubQuestion(questionnaireId, subQuestionId);
    }

    @Override
    @Transactional
    public int deleteMainQuestion(Integer questionnaireId, Integer mainQuestionId) {
        validCanEdit(questionnaireId);
        return super.deleteMainQuestion(questionnaireId, mainQuestionId);
    }

    @Override
    @Transactional
    public TemplateQuestion updateMainQuestionWithSub(Integer questionnaireId, TemplateQuestion mainQuestion) {
        validCanEdit(questionnaireId);
        return super.updateMainQuestionWithSub(questionnaireId, mainQuestion);
    }

    private void validCanEdit(Integer templateId) {
        Objects.requireNonNull(templateId);
        TemplateQuestionnaire questionnaire = dao.findQuestionnaire(templateId);
        if (questionnaire == null) {
            throw new ErrorParamException("Not found the questionnaire template.");
        }
        if (!questionnaire.editable()) {
            throw new PermissionException("Cannot delete/edit system questionnaire template.");
        }

    }
}
