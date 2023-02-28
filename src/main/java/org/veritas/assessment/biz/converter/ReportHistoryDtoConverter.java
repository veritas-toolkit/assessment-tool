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
import org.veritas.assessment.biz.dto.ReportHistoryDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.system.entity.Group;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import java.util.Objects;

@Component
@Slf4j
public class ReportHistoryDtoConverter implements Converter<ReportHistoryDto, ProjectReport> {
    @Autowired
    @Lazy
    private UserService userService;

    @Override
    public ReportHistoryDto convertFrom(ProjectReport report) {
        ReportHistoryDto dto = new ReportHistoryDto();
        dto.setVersionId(report.getVersionId());
        dto.setProjectId(report.getProjectId());
        dto.setVersionIdOfProject(report.getVersionIdOfProject());
        dto.setCreatorUserId(report.getCreatorUserId());
        dto.setCreatedTime(report.getCreatedTime());
        dto.setTag(report.getTag());
        dto.setVersion(report.getVersion());
        dto.setMessage(report.getMessage());
        User creator = userService.findUserById(report.getCreatorUserId());
        if (creator != null) {
            dto.setCreator(new UserSimpleDto(creator));
        }
        return dto;
    }
}
