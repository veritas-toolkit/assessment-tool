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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.QuestionCommentDtoConverter;
import org.veritas.assessment.biz.dto.QuestionCommentCreateDto;
import org.veritas.assessment.biz.dto.QuestionCommentDto;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionDiffDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionEditDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireDiffTocDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.QuestionnaireTocDto;
import org.veritas.assessment.biz.dto.v2.questionnaire.SimpleQuestionDto;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionnaireVersion;
import org.veritas.assessment.biz.service.CommentService;
import org.veritas.assessment.biz.service.ProjectService;
import org.veritas.assessment.biz.service.questionnaire.QuestionnaireService;
import org.veritas.assessment.common.exception.ErrorParamException;
import org.veritas.assessment.common.exception.IllegalDataException;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;

@RestController
@Slf4j
@RequestMapping("/project/{projectId}/questionnaire/compare")
public class ProjectQuestionnaireCompareController {
    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionnaireService questionnaireService;

    // fetch toc with diff
    @Operation(summary = "Fetch the diff Table of Content.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/toc")
    public QuestionnaireDiffTocDto findDiffToc(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") int projectId,
            @RequestParam(name= "based") long basedVid,
            @RequestParam(name = "new", required = false) Long newVid) {
        QuestionnaireVersion based = questionnaireService.findByQuestionnaireVid(basedVid);
        Project project = projectService.findProjectById(projectId);
        QuestionnaireVersion newVersion = null;
        if (newVid == null) {
            newVersion = questionnaireService.findLatestQuestionnaire(projectId);
        } else {
            newVersion = questionnaireService.findByQuestionnaireVid(newVid);
        }
        this.validProjectAndQuestionnaires(project, based, newVersion);

        return new QuestionnaireDiffTocDto(project, based, newVersion);
    }

    @Operation(summary = "Fetch the diff Table of Content.")
    @PreAuthorize("hasPermission(#projectId, 'project', 'read')")
    @GetMapping("/question/{questionId}")
    public QuestionDiffDto findQuestionDiff(
            @Parameter(hidden = true) User operator,
            @PathVariable("projectId") int projectId,
            @PathVariable("questionId") Long questionId,
            @RequestParam(name= "based") long basedVid,
            @RequestParam(name = "new", required = false) Long newVid) {
        Project project = projectService.findProjectById(projectId);
        QuestionnaireVersion based = questionnaireService.findByQuestionnaireVid(basedVid);
        QuestionnaireVersion newVersion = null;
        if (newVid == null) {
            newVersion = questionnaireService.findLatestQuestionnaire(projectId);
        } else {
            newVersion = questionnaireService.findByQuestionnaireVid(newVid);
        }
        this.validProjectAndQuestionnaires(project, based, newVersion);
        QuestionNode basedQuestionNode = based.findNodeByQuestionId(questionId);
        QuestionNode newQuestionNode = newVersion.findNodeByQuestionId(questionId);
        return new QuestionDiffDto(basedQuestionNode, newQuestionNode);

    }

    private void validProjectAndQuestionnaires(Project project,
                                               QuestionnaireVersion based,
                                               QuestionnaireVersion newVersion) {
        if (project == null) {
            throw new NotFoundException("Not found the project");
        }
        if (based == null) {
            throw new NotFoundException("Not found the based questionnaire.");
        }
        if (newVersion == null) {
            throw new NotFoundException("Not found the new questionnaire version.");
        }
        boolean basedVidBelongProject = Objects.equals(project.getId(), based.getProjectId());
        boolean newVidBelongProject = Objects.equals(project.getId(), newVersion.getProjectId());
        if (!basedVidBelongProject) {
            throw new IllegalRequestException("The based questionnaire is not belong current project.");
        }
        if (!newVidBelongProject) {
            throw new IllegalRequestException("The new questionnaire new is not belong current project.");
        }

    }

}
