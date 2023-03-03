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
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionEditDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireBasicDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireCreateDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireEditDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionReorderDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateQuestionnaireTocDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateSubQuestionAddDto;
import org.veritas.assessment.biz.dto.questionnaire.TemplateSubQuestionReorderDto;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.common.exception.IllegalRequestException;
import org.veritas.assessment.common.exception.NotFoundException;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
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
    @Operation(summary = "Admin: fetch a main question with of questionnaire template.")
    @GetMapping("/{templateId}/question/{questionId}")
    public TemplateQuestionDto fetchMainQuestion(User operator,
                                                         @PathVariable("templateId") Integer templateId,
                                                         @PathVariable("questionId") Integer questionId) {
        TemplateQuestionnaire questionnaire = service.findByTemplateId(templateId);
        if (questionnaire == null) {
            throw new NotFoundException("Not found the questionnaire template.");
        }
        TemplateQuestion question = questionnaire.findQuestion(questionId);
        if (question == null) {
            throw new NotFoundException("Not found the question template.");
        }
        return new TemplateQuestionDto(question);
    }
    @Operation(summary = "Admin: delete a main question with of questionnaire template.")
    @DeleteMapping("/{templateId}/question/{questionId}")
    public TemplateQuestionnaireTocDto deleteMainQuestion(User operator,
                                                          @PathVariable("templateId") Integer templateId,
                                                          @PathVariable("questionId") Integer questionId) {
        TemplateQuestionnaire templateQuestionnaire = service.deleteMainQuestion(operator, templateId, questionId);
        return new TemplateQuestionnaireTocDto(templateQuestionnaire);
    }

    @Operation(summary = "Admin: delete a sub question with of questionnaire template.")
    @DeleteMapping("/{templateId}/question/{questionId}/sub/{subQuestionId}")
    public TemplateQuestionDto deleteSubQuestion(User operator,
                                                 @PathVariable("templateId") Integer templateId,
                                                 @PathVariable("questionId") Integer questionId,
                                                 @PathVariable("subQuestionId") Integer subQuestionId) {
        TemplateQuestion templateQuestion = service.deleteSubQuestion(operator, templateId, questionId, subQuestionId);
        return new TemplateQuestionDto(templateQuestion);
    }

    @Operation(summary = "Admin: add a main question with of questionnaire template.")
    @PostMapping("/{templateId}/question/new")
    public TemplateQuestionnaireTocDto addMainQuestion(User operator,
                                                       @PathVariable("templateId") Integer templateId,
                                                       @Valid @RequestBody TemplateQuestionAddDto dto) {
        List<String> allQuestionList = new ArrayList<>();
        allQuestionList.add(dto.getQuestion());
        allQuestionList.addAll(dto.getSubQuestionList());
        TemplateQuestionnaire questionnaire = service.addMainQuestion(operator,
                templateId,
                dto.getPrinciple(),
                dto.getStep(),
                dto.getSerialOfPrinciple(), allQuestionList);
        return new TemplateQuestionnaireTocDto(questionnaire);
    }

    @Operation(summary = "Admin: add a sub question into questionnaire template.")
    @PostMapping("/{templateId}/question/{questionId}/sub/new")
    public TemplateQuestionDto addSubQuestion(User operator,
                                              @PathVariable("templateId") Integer templateId,
                                              @PathVariable("questionId") Integer questionId,
                                              @Valid @RequestBody TemplateSubQuestionAddDto dto) {
        service.addSubQuestion(operator, templateId, questionId, dto.getSubSerial(), dto.getQuestion());
        TemplateQuestionnaire questionnaire = service.findByTemplateId(templateId);
        TemplateQuestion question = questionnaire.findQuestion(questionId);
        return new TemplateQuestionDto(question);
    }

    @Operation(summary = "Admin: reorder the question of questionnaire template.")
    @PostMapping("/{templateId}/question/reorder")
    public TemplateQuestionnaireTocDto reorderMainQuestion(User operator,
                                                   @PathVariable("templateId") Integer templateId,
                                                   @Valid @RequestBody TemplateQuestionReorderDto dto) {
        TemplateQuestionnaire questionnaire = service.reorderMainQuestion(operator, templateId, dto.getPrinciple(),
                dto.getStep(), dto.getQuestionIdList());
        return new TemplateQuestionnaireTocDto(questionnaire);
    }

    @Operation(summary = "Admin: reorder the sub question of questionnaire template.")
    @PostMapping("/{templateId}/question/{questionId}/sub/reorder")
    public TemplateQuestionDto reorderSubQuestion(User operator,
                                                  @PathVariable("templateId") Integer templateId,
                                                  @PathVariable("questionId") Integer questionId,
                                                  @Valid @RequestBody TemplateSubQuestionReorderDto dto) {
        TemplateQuestionnaire q = service.reorderSubQuestion(operator, templateId, questionId, dto.getNewOrderList());
        return new TemplateQuestionDto(q.findQuestion(questionId));
    }
}
