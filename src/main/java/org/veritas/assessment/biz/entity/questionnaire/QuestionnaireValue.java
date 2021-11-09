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

package org.veritas.assessment.biz.entity.questionnaire;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Data
public abstract class QuestionnaireValue<T extends QuestionValue<T>> implements Serializable {

    private static final boolean DEFAULT_EDITABLE = true;
    private String partATitle;
    private String partBTitle;
    private String partCTitle;
    private String partDTitle;
    private String partETitle;
    @TableField(exist = false)
    private List<T> questions;

    public abstract Integer questionnaireId();

    public abstract void configQuestionnaireId(Integer questionnaireId);

    public void setQuestions(List<T> questions) {
        if (questions == null || questions.isEmpty()) {
            this.questions = Collections.emptyList();
        } else {
            List<T> list = new ArrayList<>(questions);
            list.sort(T::compareTo);
            this.questions = Collections.unmodifiableList(list);
        }
    }


    public void addQuestions(List<T> questionList) {
        questionList.forEach(q -> q.setSubQuestions(null));
        List<T> list = T.toTree2(questionList);
        this.setQuestions(list);
    }

    public List<T> allQuestionsWithSub() {
        List<T> all = new ArrayList<>();
        for (T question : questions) {
            all.add(question);
            all.addAll(question.getSubQuestions());
        }
        return all;
    }

    public void configQuestionnaireIdWithQuestion(Integer questionnaireId) {
        this.configQuestionnaireId(questionnaireId);
        for (T q : allQuestionsWithSub()) {
            q.configQuestionnaireId(questionnaireId);
        }
    }

    public List<T> findByPart(String part) {
        List<T> list = new ArrayList<>();
        for (T item : questions) {
            if (StringUtils.equals(part, item.getPart())) {
                list.add(item);
            }
        }
        return list;
    }

    public int nextPartSerial(String part) {
        List<T> questionList = findByPart(part);
        int maxPartSerial = 0;
        for (T t : questionList) {
            if (t.getPartSerial() > maxPartSerial) {
                maxPartSerial = t.getPartSerial();
            }
        }
        return maxPartSerial + 1;
    }

    public T findQuestionById(Integer questionId) {
        Objects.requireNonNull(questionId, "Arg [questionId] should not be null.");
        for (T question : allQuestionsWithSub()) {
            if (Objects.equals(questionId, question.getId())) {
                return question;
            }
        }
        return null;
    }

    public T findMainQuestionWitSubByTitle(String title) {
        Objects.requireNonNull(title, "Arg[title] should not be null.");
        Optional<T> optional = questions.stream().filter(e -> StringUtils.equals(title, e.title())).findFirst();
        return optional.orElse(null);
    }

    public T findMainQuestionWitSubByTitle(String part, Integer partSerial) {
        Objects.requireNonNull(part, "Arg[part] should not be null.");
        Objects.requireNonNull(partSerial, "Arg[partSerial] should not be null.");
        return findMainQuestionWitSubByTitle(part + partSerial);
    }

    public <P extends QuestionValue<P>, S extends QuestionnaireValue<P>> void copyFrom(S source, Supplier<T> supplier) {
        BeanUtils.copyProperties(source, this, QuestionnaireValue.class);
        List<P> sourceQuestionList = source.allQuestionsWithSub();
        List<T> list = new ArrayList<>(sourceQuestionList.size());

        for (P sourceQuestion : sourceQuestionList) {
            T question = supplier.get();
            question.copyFrom(sourceQuestion);
            list.add(question);
        }
        this.addQuestions(list);
    }

    public void configQuestionnaireIdWithSub(Integer questionnaireId) {
        this.configQuestionnaireId(questionnaireId);
        for (T t : this.allQuestionsWithSub()) {
            t.configQuestionnaireId(questionnaireId);
        }
    }


    public Date latestEditTime() {
        Date latest = null;
        for (T question : questions) {
            if (latest == null) {
                latest = question.getEditTime();
            } else if (question.getEditTime() != null) {
                latest = latest.compareTo(question.getEditTime()) > 0 ? latest : question.getEditTime();
            }
        }
        return latest;
    }

    public List<QuestionnairePartition<T>> toPartitionList() {
        return QuestionnairePartition.toPartitionList(this);
    }
}
