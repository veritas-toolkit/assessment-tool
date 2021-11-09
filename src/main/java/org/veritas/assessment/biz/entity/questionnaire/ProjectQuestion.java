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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@TableName(autoResultMap = true)
@Slf4j
@Data
@ToString(callSuper = true)
public class ProjectQuestion extends QuestionValue<ProjectQuestion> {

    private Integer projectId;

    private String answer;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date answerEditTime;

    @Override
    public Integer questionnaireId() {
        return projectId;
    }

    @Override
    public void configQuestionnaireId(Integer questionnaireId) {
        this.projectId = questionnaireId;
    }

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public boolean completed() {
        if (isSubQuestion()) {
            return this.hasAnswer();
        } else {
            List<ProjectQuestion> subList = this.getSubQuestions();
            if (subList == null || subList.isEmpty()) {
                return this.hasAnswer();
            }
            boolean someSubCompleted = false;
            for (ProjectQuestion sub : subList) {
                if (sub.isAnswerRequired() && !sub.hasAnswer()) {
                    return false;
                }
                if (sub.hasAnswer()) {
                    someSubCompleted = true;
                }
            }
            return someSubCompleted;
        }
    }

    public boolean hasAnswer() {
        if (isSubQuestion()) {
            return !StringUtils.isEmpty(this.getAnswer());
        } else {
            boolean hasSubAnswer = getSubQuestions().stream().anyMatch(ProjectQuestion::hasAnswer);
            if (hasSubAnswer) {
                return true;
            } else {
                return !StringUtils.isEmpty(this.getAnswer());
            }
        }
    }
}
