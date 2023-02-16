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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.dto.ModelArtifactDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.entity.ProjectReport;
import org.veritas.assessment.biz.entity.artifact.ModelArtifactVersion;
import org.veritas.assessment.biz.service.ModelArtifactService;
import org.veritas.assessment.biz.service.ProjectReportService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.common.exception.NotFoundException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/project/{projectId}/history/{versionIdOfProject}/")
@PreAuthorize("hasPermission(#projectId, 'project', 'read')")
public class ProjectVersionController {
    @Autowired
    private ModelArtifactService modelArtifactService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private ProjectReportService reportService;


    // {versionId}/report
    @Operation(summary = "Download the report.")
    @GetMapping("/report")
    public HttpEntity<byte[]> exportHistoryReport(
            @PathVariable("projectId") Integer projectId,
            @PathVariable("versionIdOfProject") Integer projectVersionId) {
        ProjectReport report = reportService.findReport(projectId, projectVersionId);
        if (report == null) {
            throw new NotFoundException("The report of the project.");
        }
        HttpHeaders headers = new HttpHeaders();
        String fileName = "report.pdf";
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET, POST, PUT");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
        headers.add("Content-Disposition", "filename=" + fileName);
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        byte[] content = reportService.loadReportPdf(report);
        return new HttpEntity<>(content, headers);
    }

    // json info
    @Operation(summary = "Download the model artifact file(json) of the report.")
    @GetMapping("/modelArtifact")
    public ModelArtifactDto getJsonInfo(@PathVariable("projectId") Integer projectId,
                                        @PathVariable("versionIdOfProject") Integer projectVersionId) {
        ProjectReport report = reportService.findReport(projectId, projectVersionId);
        if (report == null) {
            throw new NotFoundException("Not found the report of the project.");
        }
        ModelArtifactVersion artifact = modelArtifactService.findByVersionId(report.getModelArtifactVersionId());
        if (artifact == null) {
            throw new NotFoundException("Not found the model artifact of the report.");
        }
        return new ModelArtifactDto(artifact);
    }

    // json
    @Operation(summary = "Download the model artifact file(json) of the report.")
    @GetMapping("/modelArtifact/download")
    public HttpEntity<String> downloadJson(@PathVariable("projectId") Integer projectId,
                                           @PathVariable("versionIdOfProject") Integer projectVersionId) {
        ProjectReport report = reportService.findReport(projectId, projectVersionId);
        if (report == null) {
            throw new NotFoundException("Not found the report of the project.");
        }
        ModelArtifactVersion artifact = modelArtifactService.findByVersionId(report.getModelArtifactVersionId());
        if (artifact == null) {
            throw new NotFoundException("Not found the model artifact of the report.");
        }
        try {
            modelArtifactService.loadContent(artifact);
        } catch (IOException exception) {
            throw new NotFoundException("File not found or deleted.");
        }
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        header.set(HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=" + artifact.getFilename().replace(" ", "_"));
        header.setContentLength(artifact.getJsonContent().getBytes(StandardCharsets.UTF_8).length);
        return new HttpEntity<>(artifact.getJsonContent(), header);
    }

    @Operation(summary = "Find the questionnaire of the report.")
    @GetMapping("/questionnaire")
    public QuestionnaireTocDto findQuestionnaire(
            @PathVariable("projectId") Integer projectId,
            @PathVariable("versionIdOfProject") Integer projectVersionId) {
        ProjectReport report = reportService.findReport(projectId, projectVersionId);
        if (report == null) {
            throw new NotFoundException("Not found report of the project.");
        }
        // TODO: 2023/2/16 fetch questionnaire by vid; 
//        QuestionnaireVersion questionnaire = null;
//        if (questionnaire == null) {
//            throw new NotFoundException("Not the questionnaire.");
//        }
        return null;
    }
}
