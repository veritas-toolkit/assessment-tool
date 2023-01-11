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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

@Repository
@Slf4j
@Deprecated
public class TemplateQuestionnaireDao extends QuestionnaireValueDao<TemplateQuestion, TemplateQuestionnaire> {
    @Autowired
    private TemplateQuestionnaireMapper questionnaireMapper;

    @Autowired
    private TemplateQuestionMapper questionMapper;

    @Override
    protected QuestionnaireValueMapper<TemplateQuestionnaire> getQuestionnaireMapper() {
        return questionnaireMapper;
    }

    @Override
    protected QuestionValueMapper<TemplateQuestion> getQuestionMapper() {
        return questionMapper;
    }

    public List<TemplateQuestionnaire> findTemplateList() {
        return questionnaireMapper.findTemplateList();
    }

    public int deleteById(Integer templateId) {
        return questionnaireMapper.deleteById(templateId);
    }

    public Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword, int page, int pageSize) {
        Pageable<TemplateQuestionnaire> pageable =
                questionnaireMapper.findTemplatePageable(prefix, keyword, page, pageSize);
        pageable.getRecords().forEach(questionnaire -> {
            List<TemplateQuestion> questionList = getQuestionMapper().findByQuestionnaireId(questionnaire.questionnaireId());
            questionnaire.addQuestions(questionList);
        });
        return pageable;
    }

    public int updateBasicInfo(Integer templateId, String name, String description) {
        int result = questionnaireMapper.updateBasicInfo(templateId, name, description);
        if (result < 0) {
            throw new IllegalStateException(
                    String.format("Database update return value less than zero, value:[%d].", result));
        } else if (result == 0) {
            log.warn("There is not questionnaire template[{}]", templateId);
            throw new NotFoundException(String.format("There is not questionnaire template[%d]", templateId));
        }
        return result;
    }

}
