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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "vat_project_version_questionnaire", autoResultMap = true)
@ToString(callSuper = true)
@Deprecated
public class ProjectVersionQuestionnaire extends QuestionnaireValue<ProjectVersionQuestion> {
    @TableId(type = IdType.AUTO)
    private Integer versionId;

    private Integer projectId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    public static ProjectVersionQuestionnaire createFrom(ProjectQuestionnaire questionnaire) {
        ProjectVersionQuestionnaire version = new ProjectVersionQuestionnaire();
        version.copyFrom(questionnaire, ProjectVersionQuestion::new);
        version.setProjectId(questionnaire.getProjectId());
        version.setCreatedTime(new Date());
        for (ProjectVersionQuestion question : version.allQuestionsWithSub()) {
            question.setProjectId(questionnaire.getProjectId());
        }
        return version;
    }

    @Override
    public Integer questionnaireId() {
        return versionId;
    }

    @Override
    public void configQuestionnaireId(Integer questionnaireId) {
        this.versionId = questionnaireId;
    }

    public boolean updated(ProjectQuestionnaire questionnaire) {
        boolean titleUpdated = !StringUtils.equals(this.getPartATitle(), questionnaire.getPartATitle());
        titleUpdated |= !StringUtils.equals(this.getPartBTitle(), questionnaire.getPartBTitle());
        titleUpdated |= !StringUtils.equals(this.getPartCTitle(), questionnaire.getPartCTitle());
        titleUpdated |= !StringUtils.equals(this.getPartDTitle(), questionnaire.getPartDTitle());
        titleUpdated |= !StringUtils.equals(this.getPartETitle(), questionnaire.getPartETitle());
        if (titleUpdated) {
            return true;
        }

        boolean questionUpdated = false;
        for (ProjectVersionQuestion versionQuestion : getQuestions()) {
            ProjectQuestion question = questionnaire.findMainQuestionWitSubByTitle(versionQuestion.title());
            if (question == null) {
                questionUpdated = true;
                return true;
            }
            questionUpdated = !StringUtils.equals(versionQuestion.getAnswer(), question.getAnswer());
            questionUpdated |= !StringUtils.equals(versionQuestion.getContent(), question.getContent());
            for (ProjectVersionQuestion subVersionQuestion : versionQuestion.getSubQuestions()) {
                ProjectQuestion sub = question.findSub(subVersionQuestion.getSubSerial());
                if (sub == null) {
                    return true;
                }
                questionUpdated |= !StringUtils.equals(subVersionQuestion.getContent(), sub.getContent());
                questionUpdated |= !StringUtils.equals(subVersionQuestion.getAnswer(), sub.getAnswer());
                if (questionUpdated) {
                    return true;
                }
            }
        }
        return questionUpdated;
    }
}
