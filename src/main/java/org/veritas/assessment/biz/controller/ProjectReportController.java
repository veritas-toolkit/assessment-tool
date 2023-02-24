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

package org.veritas.assessment.biz.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.ProjectDtoConverter;
import org.veritas.assessment.biz.dto.ExportReportDto;
import org.veritas.assessment.biz.dto.ReportHistoryDto;
import org.veritas.assessment.biz.dto.SuggestionVersionDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.util.Version;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.GroupService;
import org.veritas.assessment.system.service.UserService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/report")
public class ProjectReportController {

    @Autowired
    UserService userService;

    @Autowired
    GroupService groupService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectReportService reportService;

    @Operation(summary = "List all report version.")
    @GetMapping("/list")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public List<ReportHistoryDto> list(@PathVariable("projectId") Integer projectId) {
        List<ProjectReport> reportList = reportService.findReportHistoryList(projectId);
        return ReportHistoryDto.copyFrom(reportList);
    }

    @Operation(summary = "Preview report through projectId.")
    @GetMapping(path = "/preview", produces = MediaType.TEXT_HTML_VALUE)
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public HttpEntity<String> previewReport(@PathVariable("projectId") Integer projectId) throws IOException {
        String fileName = "report.html";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_HTML);
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        Project project = projectService.findProjectById(projectId);
        String html = reportService.previewReport(project);

        return new HttpEntity<>(html, headers);
    }

    @Operation(summary = "Preview report through projectId.")
    @GetMapping(path = "/preview_pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public HttpEntity<byte[]> previewReportPdf(@PathVariable("projectId") Integer projectId) throws IOException {
        String fileName = "report_preview.pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");
        Project project = projectService.findProjectById(projectId);
        byte[] content = reportService.previewReportPdf(project);
        return new HttpEntity<>(content, headers);

    }

    @Operation(summary = "Get suggestion version.")
    @GetMapping("/suggestion-version")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public SuggestionVersionDto suggestionVersion(@PathVariable("projectId") Integer projectId) {

        SuggestionVersionDto dto = new SuggestionVersionDto();
        ProjectReport latest = reportService.findLatestReport(projectId);
        if (latest == null) {
            dto.setSuggestionVersionList(Version.SUGGESTION_FIRST_VERSION_LIST);
        } else {
            dto.setLatestVersion(latest.getVersion());
            try {
                Version version = new Version(latest.getVersion());
                dto.setSuggestionVersionList(version.nextSuggestionVersionList());
            } catch (Exception exception) {
                log.warn("The latest", exception);
                dto.setSuggestionVersionList(Version.SUGGESTION_FIRST_VERSION_LIST);
            }
        }
        return dto;
    }

    @Operation(summary = "Export report through projectId.")
    @PostMapping("/export")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public HttpEntity<byte[]> exportPdf(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody @Valid ExportReportDto exportReportDto,
            HttpServletResponse response) throws IOException {
        String fileName = "out.pdf";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "Content-Type");
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=" + fileName);
        headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
        headers.add(HttpHeaders.PRAGMA, "no-cache");

        Project project = projectService.findProjectById(projectId);
        ProjectReport report = reportService.createReport(operator, project, exportReportDto.getVersion(),
                exportReportDto.getMessage());
        byte[] content = reportService.loadReportPdf(report);
        return new HttpEntity<>(content, headers);
    }


    @Operation(summary = "Export report through projectId.")
    @PostMapping("/export2")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    public ReportHistoryDto exportPdf2(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") Integer projectId,
            @RequestBody @Valid ExportReportDto exportReportDto)
            throws IOException {

        Project project = projectService.findProjectById(projectId);
        ProjectReport report = reportService.createReport(operator, project, exportReportDto.getVersion(),
                exportReportDto.getMessage());
        return ReportHistoryDto.copyFrom(report);
    }

}
