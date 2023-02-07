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

import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Data
@Slf4j
public class QuestionCommentDto {
    private Integer id;

    private Long questionId;

    private Integer projectId;

    private Integer userId;

    private String comment;

    private Date createdTime;

    private Integer referCommentId;

    private String username;

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
}
