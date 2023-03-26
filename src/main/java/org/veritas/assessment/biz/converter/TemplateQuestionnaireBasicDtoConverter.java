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

package org.veritas.assessment.biz.converter;

import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireBasicDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;

@Component
public class TemplateQuestionnaireBasicDtoConverter
        implements Converter<TemplateQuestionnaireBasicDto, TemplateQuestionnaire> {
    @Override
    public TemplateQuestionnaireBasicDto convertFrom(TemplateQuestionnaire source) {
        if (source == null) {
            return null;
        }
        TemplateQuestionnaireBasicDto dto = new TemplateQuestionnaireBasicDto();
        dto.setTemplateId(source.getId());
        dto.setName(source.getName());
        dto.setBusinessScenario(source.getBusinessScenario());
        dto.setDescription(source.getDescription());
        dto.setCreatedTime(source.getCreatedTime());
        dto.setType(source.getType());
        return dto;
    }
}
