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

package org.veritas.assessment.biz.dto.questionnaire;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.veritas.assessment.biz.constant.QuestionnaireTemplateType;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class TemplateQuestionnaireDto {
    private Integer templateId;

    private String name;

    private int businessScenario;

    private QuestionnaireTemplateType type;

    private String description;

    private Date createdTime;

    public TemplateQuestionnaireDto(TemplateQuestionnaire questionnaire) {
        this.setTemplateId(questionnaire.getId());
        this.setName(questionnaire.getName());
        this.setType(questionnaire.getType());
        this.setDescription(questionnaire.getDescription());
        this.setCreatedTime(questionnaire.getCreatedTime());
    }
}
