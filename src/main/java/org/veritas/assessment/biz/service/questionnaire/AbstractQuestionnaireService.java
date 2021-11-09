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

package org.veritas.assessment.biz.service.questionnaire;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire.QuestionValue;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireValue;
import org.veritas.assessment.biz.mapper.questionnaire.QuestionnaireValueDao;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.NotFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractQuestionnaireService<
        T extends QuestionValue<T>,
        Q extends QuestionnaireValue<T>,
        D extends QuestionnaireValueDao<T, Q>> implements BaseQuestionnaireService<T, Q> {
    @Autowired
    private D questionnaireDao;

    @Transactional
    @Override
    public Q findQuestionnaireById(Integer questionnaireId) {
        return questionnaireDao.findQuestionnaire(questionnaireId);
    }

    @Transactional
    @Override
    public Q add(Q questionnaire) {
        return questionnaireDao.add(questionnaire);
    }

    @Transactional
    @Override
    public T addMainQuestion(Integer questionnaireId, T mainQuestion) {
        Objects.requireNonNull(questionnaireId, "Arg[questionnaireId] should not be null.");
        Objects.requireNonNull(mainQuestion, "Arg[mainQuestion] should not be null.");
        Objects.requireNonNull(mainQuestion.getPart(), "Arg[mainQuestion.part] should not be null.");
        // config questionnaire id
        if (mainQuestion.questionnaireId() == null) {
            mainQuestion.configQuestionnaireId(questionnaireId);
        } else {
            if (!Objects.equals(questionnaireId, mainQuestion.questionnaireId())) {
                throw new IllegalArgumentException(String.format(
                        "Arg [questionnaireId:%d] is different form arg [mainQuestion's questionnaireId:%d]",
                        questionnaireId, mainQuestion.questionnaireId()));
            }
        }

        Q q = questionnaireDao.findQuestionnaire(questionnaireId);

        // config partSerial
        mainQuestion.setPartSerial(q.nextPartSerial(mainQuestion.getPart()));
        mainQuestion.setSubSerial(0);
        mainQuestion.configureProperty(questionnaireId, mainQuestion.getPart(), mainQuestion.getPartSerial());

        int result = questionnaireDao.addQuestion(questionnaireId, mainQuestion);
        if (result != 1) {
            log.warn("Add main question may fail. The result is {}", result);
        }

        // sub
        List<T> subList = mainQuestion.getSubQuestions();
        if (subList != null) {
            questionnaireDao.addQuestionList(questionnaireId, subList);
        }
        return mainQuestion;
    }

    @Override
    @Transactional
    public int deleteSubQuestion(Integer questionnaireId, Integer subQuestionId) {
        Q questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
        T subQuestion = questionnaire.findQuestionById(subQuestionId);
        if (subQuestion == null || !subQuestion.isSubQuestion()) {
            throw new NotFoundException("Not such sub-question");
        }
        T mainQuestion = questionnaire.findMainQuestionWitSubByTitle(subQuestion.title());
        if (mainQuestion == null) {
            throw new IllegalStateException("Main question does not exist.");
        }
        if (!mainQuestion.isEditable() || !subQuestion.isEditable()) {
            throw new ErrorParamException("The sub question cannot be editable.");
        }
        int result = questionnaireDao.deleteQuestionById(questionnaireId, subQuestionId);
        if (result > 0) {
            fixSubSerial(questionnaireId, subQuestion.title());
        }

        return result;
    }

    @Override
    public int deleteMainQuestion(Integer questionnaireId, Integer mainQuestionId) {
        Q questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
        T question = questionnaire.findQuestionById(mainQuestionId);
        if (question == null || !question.isMainQuestion()) {
            throw new NotFoundException("Not such question.");
        }
        if (!question.isEditable()) {
            throw new ErrorParamException("Cannot delete the question.");
        }
        List<Integer> list = new ArrayList<>(question.getSubQuestions().size() + 1);
        list.add(question.getId());
        for (T subQuestion : question.getSubQuestions()) {
            list.add(subQuestion.getId());
        }
        int result = questionnaireDao.deleteQuestionById(questionnaireId, list);
        if (result > 0) {
            fixPartSerial(questionnaireId, question.getPart());
        }
        return result;
    }


    /**
     * Edit main question with all it's sub-questions.
     * <p>
     * Including: question, hint, sub-serial
     * </p>
     *
     * @param questionnaireId The id of questionnaire.
     * @param mainQuestion    Main question with subs.
     * @return The updated main question with sub.
     */
    @Transactional
    @Override
    public T updateMainQuestionWithSub(Integer questionnaireId, T mainQuestion) {
        Objects.requireNonNull(questionnaireId, "Arg[questionnaireId] should not be null.");
        Objects.requireNonNull(mainQuestion, "Arg[mainQuestion] should not be null.");
        if (mainQuestion.getId() == null) {
            throw new ErrorParamException("Cannot find the question without id.");
        }
        Q questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
        if (questionnaire == null) {
            throw new NotFoundException("Not found the questionnaire.");
        }
        T originMain = questionnaire.findQuestionById(mainQuestion.getId());
        if (originMain == null) {
            throw new NotFoundException("Not found the question in the questionnaire.");
        }
        if (!originMain.isEditable()) {
            throw new ErrorParamException("The question cannot be edited.");
        }
        String part = originMain.getPart();
        Integer partSerial = originMain.getPartSerial();

        mainQuestion.setSubSerial(0);
        mainQuestion.configureProperty(questionnaireId, part, partSerial);

        // check subs
        List<T> originList = originMain.toList();
        Map<Integer, T> originMap = originList.stream().collect(Collectors.toMap(QuestionValue::getId, e -> e));

        List<T> newList = mainQuestion.toList();

        List<T> contentUpdatedList = new ArrayList<>();
        List<T> serialChangedList = new ArrayList<>();
        List<T> toAddSubList = new ArrayList<>();
        int oldQuestionCount = 0;
        Date now = new Date();
        for (T newQuestion : newList) {
            if (newQuestion.getId() == null) {
                newQuestion.setEditTime(now);
                toAddSubList.add(newQuestion);
            } else {
                T origin = originMap.get(newQuestion.getId());
                if (origin == null) {
                    throw new ErrorParamException(String.format("sub-question[id:%d] does not belong to the question",
                            newQuestion.getId()));
                }
                ++oldQuestionCount;
                if (!origin.isEditable()) {
                    continue;
                }
                boolean contentChanged = !StringUtils.equals(origin.getContent(), newQuestion.getContent());
                boolean hintChanged = !StringUtils.equals(origin.getHint(), newQuestion.getHint());
                boolean serialChanged = !Objects.equals(origin.getSubSerial(), newQuestion.getSubSerial());
                if (contentChanged || hintChanged) {
                    newQuestion.setEditTime(now);
                    contentUpdatedList.add(newQuestion);
                } else if (serialChanged) {
                    serialChangedList.add(newQuestion);
                }
            }
        }
        if (originList.size() != oldQuestionCount) {
            throw new ErrorParamException("Some sub questions are missing.");
        }
        // fix main
        if (!contentUpdatedList.isEmpty() || !serialChangedList.isEmpty() || !toAddSubList.isEmpty()) {
            if (!contentUpdatedList.contains(mainQuestion)) {
                contentUpdatedList.add(mainQuestion);
            }
            questionnaireDao.updateQuestion(questionnaireId, contentUpdatedList);
            questionnaireDao.updateQuestionSerial(serialChangedList);
            questionnaireDao.addQuestionList(questionnaireId, toAddSubList);
            questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
            return questionnaire.findMainQuestionWitSubByTitle(part, partSerial);
        } else {
            return originMain;
        }

    }

    private void fixPartSerial(Integer questionnaireId, String part) {
        Q questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
        if (questionnaire == null) {
            return;
        }
        List<T> mainQuestionList = questionnaire.findByPart(part);
        if (mainQuestionList == null || mainQuestionList.isEmpty()) {
            return;
        }
        int serial = 1;
        List<T> toFixList = new ArrayList<>();
        for (T question : mainQuestionList) {
            if (serial != question.getPartSerial()) {
                List<T> list = question.toList();
                for (T e : list) {
                    e.setPartSerial(serial);
                }
                toFixList.addAll(list);
            }
            ++serial;
        }
        questionnaireDao.updateQuestionSerial(toFixList);
    }

    private void fixSubSerial(Integer questionnaireId, String questionTitle) {
        Q questionnaire = questionnaireDao.findQuestionnaire(questionnaireId);
        if (questionnaire == null) {
            return;
        }
        T question = questionnaire.findMainQuestionWitSubByTitle(questionTitle);
        if (question == null) {
            return;
        }
        int serial = 1;
        List<T> toFixList = new ArrayList<>();
        for (T sub : question.getSubQuestions()) {
            if (serial != sub.getSubSerial()) {
                sub.setSubSerial(serial);
                toFixList.add(sub);
            }
            ++serial;
        }
        questionnaireDao.updateQuestionSerial(toFixList);
    }
}
