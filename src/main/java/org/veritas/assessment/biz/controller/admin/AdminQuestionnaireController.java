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

package org.veritas.assessment.biz.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.TemplateQuestionnaireBasicDtoConverter;


import org.veritas.assessment.biz.converter.TemplateQuestionnaireDtoConverter;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionEditDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireBasicDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireCreateDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireEditDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireTocDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import java.util.Objects;

@RestController
@Slf4j
@RequestMapping("/admin/questionnaire")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminQuestionnaireController {
    @Autowired
    private TemplateQuestionnaireService service;

    @Autowired
    private TemplateQuestionnaireDtoConverter dtoConverter;


    @Autowired
    private TemplateQuestionnaireBasicDtoConverter basicDtoConverter;

    @Operation(summary = "Admin: list questionnaire template pageable.")
    @GetMapping("")
    public Pageable<TemplateQuestionnaireBasicDto> listQuestionnaireTemplate(
            @RequestParam(name = "prefix", required = false) String prefix,
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "businessScenario", required = false) Integer businessScenario,
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "pageSize", defaultValue = "20") Integer pageSize) {
        Pageable<TemplateQuestionnaire> pageable = service.findTemplatePageable(prefix, keyword, businessScenario, page, pageSize);
        return basicDtoConverter.convertFrom(pageable);
    }

    @Operation(summary = "Admin: get questionnaire table of content.")
    @GetMapping("/{templateId}/toc")
    public TemplateQuestionnaireTocDto findQuestionnaireById(
            @PathVariable("templateId") Integer templateId) {
        TemplateQuestionnaire questionnaire = service.findByTemplateId(templateId);
        return new TemplateQuestionnaireTocDto(questionnaire);
    }


    // create
    @Operation(summary = "Admin: create a new questionnaire template.")
    @PutMapping("")
    public TemplateQuestionnaireDto create(User operator,
                                           @Valid @RequestBody TemplateQuestionnaireCreateDto dto) {
        TemplateQuestionnaire questionnaire =
                service.create(operator, dto.getBasicTemplateId(), dto.getName(), dto.getDescription());
        return dtoConverter.convertFrom(questionnaire);
    }

    @Operation(summary = "Admin: delete questionnaire template")
    @DeleteMapping("/{templateId}")
    public void deleteQuestionnaireById(@PathVariable("templateId") Integer templateId) {
        service.delete(templateId);
    }

    // edit basic info
    @Operation(summary = "Admin: update questionnaire template name and description.")
    @PostMapping("/{templateId}/basic")
    public TemplateQuestionnaireDto editBasicInfo(User operator,
                                                  @PathVariable("templateId") Integer templateId,
                                                  @Valid @RequestBody TemplateQuestionnaireEditDto dto) {
        if (!Objects.equals(templateId, dto.getTemplateId())) {
            throw new IllegalRequestException();
        }
        TemplateQuestionnaire questionnaire =
                service.updateBasicInfo(operator, templateId, dto.getName(), dto.getDescription());
        return dtoConverter.convertFrom(questionnaire);
    }
    @Operation(summary = "Admin: edit a question (main or sub) with of questionnaire template.")
    @PostMapping("/{templateId}/question")
    public TemplateQuestionDto updateQuestion(User operator,
                                              @PathVariable("templateId") Integer templateId,
                                              @Valid @RequestBody TemplateQuestionEditDto dto) {
        TemplateQuestion question = service.updateQuestionContent(operator, templateId, dto.getId(), dto.getContent());
        return new TemplateQuestionDto(question);
    }
/*
    // add main question
    @Operation(summary = "Admin: add a question with subs into questionnaire template.")
    @PutMapping("/{templateId}/question")
    public TemplateQuestion addMainQuestion(
            @PathVariable("templateId") Integer templateId,
            @RequestBody QuestionDto dto) {
        TemplateQuestion question = new TemplateQuestion();
        question.copyFrom(dto, TemplateQuestion::new);
        question.setTemplateId(templateId);
        question.setSubSerial(0);
        return service.addMainQuestion(templateId, question);
    }

    @Operation(summary = "Admin: delete question(main/sub) from questionnaire template.")
    @DeleteMapping("/{templateId}/question")
    public TemplateQuestionnaireDto deleteQuestion(
            @PathVariable("templateId") Integer templateId,
            @RequestParam(name = "questionId", required = false) Integer questionId,
            @RequestParam(name = "subQuestionId", required = false) Integer subQuestionId) {
        if (subQuestionId != null) {
            service.deleteSubQuestion(templateId, subQuestionId);
        } else if (questionId != null) {
            service.deleteMainQuestion(templateId, questionId);
        } else {
            throw new ErrorParamException(
                    "Error param: At least one value exists for [questionId] and [subQuestionId]");
        }
        TemplateQuestionnaire questionnaire = service.findQuestionnaireById(templateId);
        return dtoConverter.convertFrom(questionnaire);
    }


     */
}
