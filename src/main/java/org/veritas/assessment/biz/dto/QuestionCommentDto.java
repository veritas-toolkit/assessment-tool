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

package org.veritas.assessment.biz.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Data
@Slf4j
public class QuestionCommentDto {
    private Integer id;

    private Long questionId;

    private Long mainQuestionId;

    // main question content.
    private String mainQuestion;

    private Integer projectId;

    private String projectName;

    // user or group name
    private String projectOwner;

    private Integer userId;

    private String comment;

    private Date createdTime;

    private Integer referCommentId;

    // comment creator's username
    private String username;

    // comment creator's full name.
    private String userFullName;

    private boolean hasRead = false;

    public void fillHasRead(Map<Long, QuestionCommentReadLog> readLogMap) {
        if (readLogMap == null || readLogMap.isEmpty()) {
            this.setHasRead(false);
            return;
        }
        QuestionCommentReadLog readLog = readLogMap.get(questionId);
        if (readLog == null) {
            this.setHasRead(false);
            return;
        }
        boolean sameProject = Objects.equals(projectId, readLog.getProjectId());
        boolean sameQuestion = Objects.equals(questionId, readLog.getQuestionId());
        if (sameProject && sameQuestion) {
            this.hasRead = id.compareTo(readLog.getLatestReadCommentId()) <= 0;
        } else {
            this.setHasRead(false);
        }
    }

    public static Map<Long, List<QuestionCommentDto>> toMapList(List<QuestionCommentDto> dtoList) {
        if (dtoList == null || dtoList.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Long, List<QuestionCommentDto>> map = new TreeMap<>();
        for (QuestionCommentDto dto : dtoList) {
            List<QuestionCommentDto> qDtoList = map.computeIfAbsent(dto.getQuestionId(), k -> new ArrayList<>());
            qDtoList.add(dto);
        }
        return map;
    }
}
