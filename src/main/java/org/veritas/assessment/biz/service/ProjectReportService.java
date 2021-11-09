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

package org.veritas.assessment.biz.service;

import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.system.entity.User;

import java.io.IOException;
import java.util.List;

public interface ProjectReportService {

    String previewReport(Project project) throws IOException;

    byte[] previewReportPdf(Project project) throws IOException;

    ProjectReport createReport(User operator, Project project, String version, String message) throws IOException;

    ProjectReport findLatestReport(Integer projectId);

    byte[] loadReportPdf(ProjectReport report);

    ProjectReport findReport(Integer projectId, Integer projectVersionId);

    List<ProjectReport> findReportHistoryList(Integer projectId);

}
