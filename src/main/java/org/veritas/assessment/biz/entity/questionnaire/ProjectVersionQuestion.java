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
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;

@EqualsAndHashCode(callSuper = true)
@TableName(autoResultMap = true)
@Slf4j
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class ProjectVersionQuestion extends QuestionValue<ProjectVersionQuestion> {

    private Integer projectId;

    private Integer questionnaireVersionId;

    private Integer questionId;

    private String answer;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date answerEditTime;

    public ProjectVersionQuestion(ProjectQuestion question) {
        super(question, ProjectVersionQuestion::new);
        this.setId(null);
        this.setProjectId(question.getProjectId());
        this.setQuestionId(question.getId());
        this.setAnswer(question.getAnswer());
        this.setAnswerEditTime(question.getAnswerEditTime());
    }

//    public ProjectVersionQuestion(ProjectQuestion question, Function<ProjectQuestion, ProjectVersionQuestion> creator) {
//        super(question, creator);
//    }

    @Override
    public Integer questionnaireId() {
        return this.getQuestionnaireVersionId();
    }

    @Override
    public void configQuestionnaireId(Integer questionnaireId) {
        this.questionnaireVersionId = questionnaireId;
    }

    @Override
    public <S extends QuestionValue<S>> void copyFrom(S source, Supplier<ProjectVersionQuestion> subSupplier) {
        super.copyFrom(source, subSupplier);
        if (source instanceof ProjectQuestion) {
            this.copyProjectProperties((ProjectQuestion) source);
        }
    }


    @Override
    public <S extends QuestionValue<S>> void copyFrom(S source) {
        super.copyFrom(source);
        if (source instanceof ProjectQuestion) {
            this.copyProjectProperties((ProjectQuestion) source);
        }
    }

    private void copyProjectProperties(ProjectQuestion projectQuestion) {
        this.setId(null);
        this.setProjectId(projectQuestion.getProjectId());
        this.setQuestionId(projectQuestion.getId());
        this.setAnswer(projectQuestion.getAnswer());
        this.setAnswerEditTime(projectQuestion.getAnswerEditTime());

        if (projectQuestion.isMainQuestion()) {
            List<ProjectVersionQuestion> versionSubList = this.getSubQuestions();
            Iterator<ProjectVersionQuestion> iterator = versionSubList.iterator();
            Iterator<ProjectQuestion> projectQuestionIterator = projectQuestion.getSubQuestions().listIterator();
            while (iterator.hasNext() && projectQuestionIterator.hasNext()) {
                ProjectQuestion originSub = projectQuestionIterator.next();
                ProjectVersionQuestion sub = iterator.next();
                sub.copyProjectProperties(originSub);
            }
        }
    }

    public boolean hasAnswer() {
        if (isSubQuestion()) {
            return !StringUtils.isEmpty(this.getAnswer());
        } else {
            boolean hasSubAnswer = getSubQuestions().stream().anyMatch(ProjectVersionQuestion::hasAnswer);
            if (hasSubAnswer) {
                return true;
            } else {
                return !StringUtils.isEmpty(this.getAnswer());
            }
        }
    }
}
