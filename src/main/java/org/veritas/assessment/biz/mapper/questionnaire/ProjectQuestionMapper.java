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

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestion;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.List;
import java.util.function.Supplier;

@Repository
@Deprecated
public interface ProjectQuestionMapper extends QuestionValueMapper<ProjectQuestion> {

    @Override
    default Class<ProjectQuestion> getEntityClass() {
        return ProjectQuestion.class;
    }

    @Override
    default Supplier<ProjectQuestion> questionSupplier() {
        return ProjectQuestion::new;
    }

    @Override
    default SFunction<ProjectQuestion, Integer> questionnaireIdFunction() {
        return ProjectQuestion::getProjectId;
    }


    // edit answer
    default int updateAnswerList(List<ProjectQuestion> questionList) {
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        int result = 0;
        Date now = new Date();
        for (ProjectQuestion question : questionList) {
            question.setAnswerEditTime(now);
            LambdaUpdateWrapper<ProjectQuestion> wrapper = new LambdaUpdateWrapper<>();
            wrapper.eq(ProjectQuestion::getId, question.getId());
            wrapper.eq(ProjectQuestion::getProjectId, question.getProjectId());
//            wrapper.ne(ProjectQuestion::getAnswer, question.getAnswer());

            wrapper.set(ProjectQuestion::getAnswer, question.getAnswer());
            wrapper.set(ProjectQuestion::getAnswerEditTime, TimestampHandler.toDbString(question.getAnswerEditTime()));
            result += update(null, wrapper);
        }
        return result;
    }


}
