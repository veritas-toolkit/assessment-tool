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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.dto.questionnaire.QuestionnaireRecordDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

@Component
@Slf4j
public class QuestionnaireRecordDtoConverter implements Converter<QuestionnaireRecordDto, QuestionnaireVersion> {
    @Autowired
    private UserService userService;

    @Override
    public QuestionnaireRecordDto convertFrom(QuestionnaireVersion source) {
        QuestionnaireRecordDto dto =  new QuestionnaireRecordDto(source);
        User user = userService.findUserById(source.getCreatorUserId());
        UserSimpleDto simpleDto = new UserSimpleDto(user);
        dto.setCreator(simpleDto);
        return dto;
    }
}
