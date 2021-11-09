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
import org.veritas.assessment.biz.entity.ProjectReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ReportHistoryDto {
    private Integer versionId;

    private Integer projectId;

    private Integer versionIdOfProject;

    private Integer creatorUserId;

    private Date createdTime;

    private String tag;

    private String message;

    private String version;

    public ReportHistoryDto(ProjectReport report) {
        this.setVersionId(report.getVersionId());
        this.setProjectId(report.getProjectId());
        this.setVersionIdOfProject(report.getVersionIdOfProject());
        this.setCreatorUserId(report.getCreatorUserId());
        this.setCreatedTime(report.getCreatedTime());
        this.setTag(report.getTag());
        this.setVersion(report.getVersion());
        this.setMessage(report.getMessage());
    }

    public static List<ReportHistoryDto> copyFrom(List<ProjectReport> reportList) {
        if (reportList == null || reportList.isEmpty()) {
            return Collections.emptyList();
        }
        return reportList.stream().map(ReportHistoryDto::new).collect(Collectors.toList());
    }

}
