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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.QuestionValue;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@Repository
public interface QuestionValueMapper<T extends QuestionValue<T>> extends BaseMapper<T> {

    default T findById(Integer id) {
        return selectById(id);
    }

    Class<T> getEntityClass();

    Supplier<T> questionSupplier();

    SFunction<T, Integer> questionnaireIdFunction();

    default List<T> findByQuestionnaireId(Integer questionnaireId) {
        Objects.requireNonNull(questionnaireId, "Arg[questionnaireId] should not null.");
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<>();
        wrapper.setEntityClass(getEntityClass());
        wrapper.eq(questionnaireIdFunction(), questionnaireId);
        wrapper.orderByAsc(T::getPart);
        wrapper.orderByAsc(T::getPartSerial);
        wrapper.orderByAsc(T::getSubSerial);
        return selectList(wrapper);
    }

    default int addQuestionList(List<T> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        Date now = new Date();
        for (T question : questionList) {
            question.setEditTime(now);
            result = result + insert(question);
        }
        return result;
    }

    default int updateQuestionContentList(List<T> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        Date now = new Date();
        T temp = questionSupplier().get();
        for (T question : questionList) {
            question.setEditTime(now);
            temp.setId(question.getId());
            temp.setContent(question.getContent());
            temp.setHint(question.getHint());
            temp.setEditTime(question.getEditTime());
            result += updateById(temp);
        }
        return result;
    }

    default int updateQuestionSerial(List<T> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        for (T question : questionList) {
            LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
            wrapper.setEntityClass(getEntityClass());
            wrapper.set(T::getPartSerial, question.getPartSerial());
            wrapper.set(T::getSubSerial, question.getSubSerial());
            wrapper.eq(T::getId, question.getId());
            result += update(null, wrapper);
        }
        return result;
    }

    default int deleteQuestionWithSub(Integer questionnaireId, String part, Integer partSerial) {
        Objects.requireNonNull(questionnaireId, "Arg[questionnaireId] should not null.");
        Objects.requireNonNull(part, "Arg[part] should not null.");
        Objects.requireNonNull(partSerial, "Arg[partSerial] should not null.");
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setEntityClass(getEntityClass());
        wrapper.eq(T::questionnaireId, questionnaireId);
        wrapper.eq(T::getPart, part);
        wrapper.eq(T::getPartSerial, partSerial);
        return delete(wrapper);
    }

    default int deleteSubQuestion(Integer questionnaireId, String part, Integer partSerial, Integer subSerial) {
        Objects.requireNonNull(questionnaireId, "Arg[questionnaireId] should not null.");
        Objects.requireNonNull(part, "Arg[part] should not null.");
        Objects.requireNonNull(partSerial, "Arg[partSerial] should not null.");
        Objects.requireNonNull(subSerial, "Arg[subSerial] should not null.");
        if (subSerial <= 0) {
            throw new IllegalArgumentException("Sub question's [subSerial] should be greater than 0.");
        }
        LambdaUpdateWrapper<T> wrapper = new LambdaUpdateWrapper<>();
        wrapper.setEntityClass(getEntityClass());
        wrapper.eq(T::questionnaireId, questionnaireId);
        wrapper.eq(T::getPart, part);
        wrapper.eq(T::getPartSerial, partSerial);
        wrapper.eq(T::getSubSerial, subSerial);
        return delete(wrapper);
    }


}
