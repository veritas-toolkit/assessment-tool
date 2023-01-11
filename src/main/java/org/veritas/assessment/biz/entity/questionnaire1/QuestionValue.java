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

package org.veritas.assessment.biz.entity.questionnaire1;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.biz.constant.QuestionType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Data
@Slf4j
@NoArgsConstructor
@Deprecated
public abstract class QuestionValue<T extends QuestionValue<T>> implements Comparable<T>, Serializable {

    public static final Integer MAIN_QUESTION_SUB_SERIAL = 0;
    private static final boolean DEFAULT_EDITABLE = true;
    private static final boolean DEFAULT_ANSWER_REQUIRED = false;
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String part;
    private Integer partSerial;
    @JsonIgnore
    private Integer subSerial;
    private String content;
    private String hint;
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date editTime;
    private boolean editable = DEFAULT_EDITABLE;
    private boolean answerRequired = DEFAULT_ANSWER_REQUIRED;
    @TableField(exist = false)
    private List<T> subQuestions;

    protected <S extends QuestionValue<S>> QuestionValue(S source) {
        Objects.requireNonNull(source);
        BeanUtils.copyProperties(source, this, QuestionValue.class);
    }

    protected <S extends QuestionValue<S>> QuestionValue(S source, Function<S, T> creator) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(creator);
        BeanUtils.copyProperties(source, this, QuestionValue.class);
        List<S> sourceSubList = source.getSubQuestions();
        if (source.isMainQuestion()) {
            if (sourceSubList == null || sourceSubList.isEmpty()) {
                this.setSubQuestions(null);
                return;
            }
            List<T> subList = new ArrayList<>(sourceSubList.size());
            for (S sourceSub : sourceSubList) {
                T sub = creator.apply(sourceSub);
                subList.add(sub);
            }
            this.setSubQuestions(subList);
        }
    }

    public static <T extends QuestionValue<T>> List<T> toTree2(List<T> list) {
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        List<T> mainList = list.stream()
                .filter(QuestionValue::isMainQuestion)
                .sorted()
                .collect(Collectors.toList());
        Map<String, T> mainMap = mainList.stream().collect(Collectors.toMap(QuestionValue::title, e -> e));

        list.stream().filter(QuestionValue::isSubQuestion)
                .sorted()
                .forEach(e -> {
                    T main = mainMap.get(e.title());
                    if (main == null) {
                        log.warn("");
                    } else {
                        main.addSub(e);
                    }
                });
        return mainList;
    }

    public abstract Integer questionnaireId();

    public abstract void configQuestionnaireId(Integer questionnaireId);

    public List<T> getSubQuestions() {
        if (this.type() == QuestionType.SUB_QUESTION) {
            return null;
        } else if (subQuestions == null) {
            return Collections.emptyList();
        } else {
            return Collections.unmodifiableList(subQuestions);
        }
    }

    public void setSubQuestions(List<T> subQuestions) {
        if (subQuestions == null || subQuestions.isEmpty()) {
            this.subQuestions = null;
        } else {
            this.subQuestions = new LinkedList<>(subQuestions);
        }
    }

    public void configureProperty(Integer questionnaireId, String part, Integer partSerial) {
        this.configQuestionnaireId(questionnaireId);
        this.setPart(part);
        this.setPartSerial(partSerial);
        if (subQuestions != null) {
            int serial = 1;
            for (T subQuestion : subQuestions) {
                subQuestion.configQuestionnaireId(questionnaireId);
                subQuestion.setPart(part);
                subQuestion.setPartSerial(partSerial);
                subQuestion.setSubSerial(serial);
                ++serial;
            }
        }
    }

    public int nextSubSerial() {
        if (!isMainQuestion()) {
            throw new IllegalStateException();
        }
        Integer max = 0;
        for (T subQuestion : getSubQuestions()) {
            if (max.compareTo(subQuestion.getSubSerial()) < 0) {
                max = subQuestion.getSubSerial();
            }
        }
        return max + 1;
    }

    @Override
    public int compareTo(QuestionValue o) {

        boolean bothNull = this.questionnaireId() == null && o.questionnaireId() == null;
        boolean same = Objects.equals(this.questionnaireId(), o.questionnaireId());
        if (!bothNull && !same) {
            throw new IllegalStateException(String.format(
                    "Property[templateId] should be same.this->[%d], other->[%d]",
                    this.questionnaireId(), o.questionnaireId()));
        }
        if (!StringUtils.equals(this.part, o.getPart())) {
            return StringUtils.compare(this.part, o.getPart());
        } else if (!Objects.equals(this.partSerial, o.getPartSerial())) {
            return Integer.compare(this.partSerial, o.getPartSerial());
        } else {
            return Integer.compare(this.subSerial, o.getSubSerial());
        }
    }

    public <S extends QuestionValue<S>> void copyFrom(S source) {
        BeanUtils.copyProperties(source, this, QuestionValue.class);
        this.setSubQuestions(null);
    }

    public <S extends QuestionValue<S>> void copyFrom(S source, Supplier<T> subSupplier) {
        BeanUtils.copyProperties(source, this, QuestionValue.class);
        List<S> sourceSubList = source.getSubQuestions();
        if (sourceSubList == null || sourceSubList.isEmpty()) {
            this.setSubQuestions(null);
            return;
        }
        List<T> subList = new ArrayList<>(sourceSubList.size());
        for (S sourceSub : sourceSubList) {
            T sub = subSupplier.get();
            sub.copyFrom(sourceSub);
            subList.add(sub);
        }
        this.setSubQuestions(subList);
    }

    public <S extends QuestionValue<S>> void copyFrom(S source, Function<S, T> creator) {
        copyFrom(source);
        List<S> sourceSubList = source.getSubQuestions();
        if (sourceSubList == null || sourceSubList.isEmpty()) {
            this.setSubQuestions(null);
            return;
        }
        List<T> subList = new ArrayList<>(sourceSubList.size());
        for (S sourceSub : sourceSubList) {
            T sub = creator.apply(sourceSub);
            sub.copyFrom(sourceSub);
            subList.add(sub);
        }
        this.setSubQuestions(subList);
    }

    public QuestionType type() {
        if (this.subSerial == null || this.subSerial.equals(MAIN_QUESTION_SUB_SERIAL)) {
            return QuestionType.MAIN_QUESTION;
        } else if (this.subSerial > 0) {
            return QuestionType.SUB_QUESTION;
        } else {
            throw new IllegalStateException("Property[subSerial] should not be negative.");
        }
    }

    @JsonIgnore
    public boolean isMainQuestion() {
        return QuestionType.MAIN_QUESTION == this.type();
    }

    @JsonIgnore
    public boolean isSubQuestion() {
        return QuestionType.SUB_QUESTION == this.type();
    }

    public String title() {
        return this.getPart() + this.getPartSerial();
    }

    public void addSub(T sub) {
        if (this.type() != QuestionType.MAIN_QUESTION) {
            throw new IllegalStateException();
        }
        if (subQuestions == null) {
            subQuestions = new LinkedList<>();
            subQuestions.add(sub);
        } else {
            ListIterator<T> iterator = subQuestions.listIterator();
            boolean hasAdd = false;
            while (iterator.hasNext()) {
                T curr = iterator.next();
                if (curr.compareTo(sub) > 0) {
                    iterator.add(sub);
                    hasAdd = true;
                    break;
                }
            }
            if (!hasAdd) {
                subQuestions.add(sub);
            }
        }
    }

    public T findSub(int subSerial) {
        if (!this.isMainQuestion()) {
            throw new UnsupportedOperationException();
        }
        if (subSerial == 0) {
            @SuppressWarnings("unchecked")
            T self = (T) this;
            return self;
        }
        for (T sub : subQuestions) {
            if (sub.getSubSerial() == subSerial) {
                return sub;
            }
        }
        return null;
    }

    public int subCount() {
        if (subQuestions == null) {
            return 0;
        } else {
            return subQuestions.size();
        }
    }

    public List<T> toList() {
        List<T> list = new ArrayList<>(this.getSubQuestions().size() + 1);
        @SuppressWarnings("unchecked")
        T self = (T) this;
        list.add(self);
        list.addAll(this.getSubQuestions());
        return list;
    }
}