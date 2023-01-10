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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@TableName(value = "vat_template_questionnaire", autoResultMap = true)
@ToString(callSuper = true)
@Deprecated
@AllArgsConstructor
@NoArgsConstructor
public class TemplateQuestionnaire extends QuestionnaireValue<TemplateQuestion> {
    @TableId(type = IdType.AUTO)
    private Integer templateId;

    private String name;

//    @TableField(value = "type", jdbcType = JdbcType.INTEGER)
    private QuestionnaireTemplateType type;

    private String description;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    @Override
    public Integer questionnaireId() {
        return templateId;
    }

    @Override
    public void configQuestionnaireId(Integer questionnaireId) {
        this.templateId = questionnaireId;
    }

    public boolean editable() {
        if (QuestionnaireTemplateType.SYSTEM == type) {
            return false;
        } else if (QuestionnaireTemplateType.USER_DEFINED == type) {
            return true;
        } else {
            throw new IllegalStateException("The property[type] is [%s] null or unknown.");
        }
    }

    public Integer getTemplateId() {
        return templateId;
    }

    public void setTemplateId(Integer templateId) {
        this.templateId = templateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public QuestionnaireTemplateType getType() {
        return type;
    }

    public void setType(QuestionnaireTemplateType type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }
}
