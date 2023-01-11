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

package org.veritas.assessment.biz.mapper.questionnaire1;

import lombok.extern.slf4j.Slf4j;
import org.veritas.assessment.biz.entity.questionnaire1.QuestionValue;
import org.veritas.assessment.biz.entity.questionnaire1.QuestionnaireValue;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Deprecated
public abstract class QuestionnaireValueDao<T extends QuestionValue<T>, Q extends QuestionnaireValue<T>> {

    protected abstract QuestionnaireValueMapper<Q> getQuestionnaireMapper();

    protected abstract QuestionValueMapper<T> getQuestionMapper();

    public Q add(Q questionnaire) {
        int result = getQuestionnaireMapper().insert(questionnaire);
        if (result <= 0) {
            log.warn("Add questionnaire record may failed.");
        }
        List<T> questionList = questionnaire.allQuestionsWithSub();
        questionList.forEach(e -> e.configQuestionnaireId(questionnaire.questionnaireId()));
        result = getQuestionMapper().addQuestionList(questionList);
        if (result != questionList.size()) {
            log.warn("Add question list may failed.");
        }

        return questionnaire;
    }

    public int addQuestion(Integer questionnaireId, T question) {
        if (!Objects.equals(questionnaireId, question.questionnaireId())) {
            throw new IllegalArgumentException("Arg[questionnaireId] should be same as [question.questionnaire]");
        }
        return getQuestionMapper().addQuestionList(Collections.singletonList(question));
    }

    public int deleteQuestionById(Integer questionnaireId, Integer questionId) {
        Objects.requireNonNull(questionnaireId);
        if (questionId == null) {
            return 0;
        }
        return deleteQuestionById(questionnaireId, Collections.singletonList(questionId));
    }

    public int deleteQuestionById(Integer questionnaireId, List<Integer> questionIdList) {
        Objects.requireNonNull(questionnaireId);
        if (questionIdList == null || questionIdList.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (Integer id : questionIdList) {
            result += getQuestionMapper().deleteById(id);
        }
        if (result != questionIdList.size()) {
            log.warn("Delete database records return value is [{}], not equal to the list size[{}]",
                    result, questionIdList.size());
        }
        return result;
    }

    public int addQuestionList(Integer questionnaireId, List<T> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        for (T question : questionList) {
            if (!Objects.equals(questionnaireId, question.questionnaireId())) {
                throw new IllegalArgumentException(
                        "Arg[questionnaireId] should be same as [questionList[*].questionnaire]");
            }
        }
        int result = getQuestionMapper().addQuestionList(questionList);
        if (result != questionList.size()) {
            log.warn("Update result[{}] is not equal to the arg list size[{}. Some database error may happened.",
                    result, questionList.size());
        }
        return result;
    }

    public int deleteSubQuestion(Integer questionnaireId, String part, Integer partSerial, Integer subSerial) {
        return getQuestionMapper().deleteSubQuestion(questionnaireId, part, partSerial, subSerial);
    }

    public int deleteQuestionWithSub(Integer questionnaireId, String part, Integer partSerial) {
        return getQuestionMapper().deleteQuestionWithSub(questionnaireId, part, partSerial);
    }

    public int updateQuestion(Integer questionnaireId, List<T> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        for (T question : questionList) {
            if (!Objects.equals(questionnaireId, question.questionnaireId())) {
                throw new IllegalArgumentException(
                        "Arg[questionnaireId] should be same as [questionList[*].questionnaire]");
            }
        }
        int result = getQuestionMapper().updateQuestionContentList(questionList);
        if (result != questionList.size()) {
            log.warn("Update result[{}] is not equal to the arg list size[{}. Some database error may happened.",
                    result, questionList.size());
        }
        return result;
    }

    public int updateQuestionSerial(List<T> questionList) {
        int result = getQuestionMapper().updateQuestionSerial(questionList);
        if (result != questionList.size()) {
            log.warn("Update result[{}] is not equal to the arg list size[{}. Some database error may happened.",
                    result, questionList.size());
        }
        return result;
    }

    public Q findQuestionnaire(Integer questionnaireId) {
        Q questionnaire = getQuestionnaireMapper().findById(questionnaireId);
        if (questionnaire == null) {
            return null;
        }
        List<T> list = getQuestionMapper().findByQuestionnaireId(questionnaireId);
        questionnaire.addQuestions(list);
        return questionnaire;
    }
}
