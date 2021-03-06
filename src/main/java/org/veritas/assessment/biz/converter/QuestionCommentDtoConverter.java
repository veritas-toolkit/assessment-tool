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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestionComment;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.Objects;

@Component
@Slf4j
public class QuestionCommentDtoConverter implements Converter<QuestionCommentDto, ProjectQuestionComment> {
    @Autowired
    @Lazy
    private UserService userService;


    @Override
    public QuestionCommentDto convertFrom(ProjectQuestionComment comment) {
        Objects.requireNonNull(comment, "The arg[comment] cannot be null.");
        QuestionCommentDto dto = new QuestionCommentDto();
        dto.setId(comment.getId());
        dto.setQuestionId(comment.getQuestionId());
        dto.setProjectId(comment.getProjectId());
        dto.setUserId(comment.getUserId());
        dto.setComment(comment.getComment());
        dto.setCreatedTime(comment.getCreatedTime());
        dto.setReferCommentId(comment.getReferCommentId());

        if (comment.getUserId() != null) {
            User user = userService.findUserById(comment.getUserId());
            if (user != null) {
                dto.setUsername(user.getUsername());
                dto.setUserFullName(user.getFullName());
            }
        }
        return dto;
    }
}
