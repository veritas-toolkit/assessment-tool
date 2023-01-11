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

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value = "vat_template_question", autoResultMap = true)
@ToString(callSuper = true)
@Slf4j
@Deprecated
public class TemplateQuestion extends QuestionValue<TemplateQuestion> {

    private Integer templateId;

    @Override
    public Integer questionnaireId() {
        return templateId;
    }

    @Override
    public void configQuestionnaireId(Integer questionnaireId) {
        this.templateId = questionnaireId;
    }
}
