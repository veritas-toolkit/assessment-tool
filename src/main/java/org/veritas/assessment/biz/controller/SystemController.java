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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.veritas.assessment.biz.converter.TemplateQuestionnaireBasicDtoConverter;
import org.veritas.assessment.biz.dto.TemplateQuestionnaireBasicDto;
import org.veritas.assessment.biz.entity.BusinessScenario;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.biz.service.SystemService;
import org.veritas.assessment.biz.service.questionnaire.TemplateQuestionnaireService;
import org.veritas.assessment.system.service.SystemConfigService;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/system")
public class SystemController {

    @Autowired
    TemplateQuestionnaireService templateQuestionnaireService;
    @Autowired
    TemplateQuestionnaireBasicDtoConverter questionnaireBasicDtoConverter;

    @Autowired
    private SystemService systemService;

    @Autowired
    private SystemConfigService systemConfigService;

    @GetMapping("/business_scenario")
    public List<BusinessScenario> getBizScenarioList() {
        return systemService.findAllBusinessScenarioList();
    }

    @GetMapping("/questionnaire_template")
    public List<TemplateQuestionnaireBasicDto> templateQuestionnaireList() {
        List<TemplateQuestionnaire> list = templateQuestionnaireService.findTemplateList();
        if (list == null || list.isEmpty()) {
            log.error("There is no questionnaire template.");
        }
        return questionnaireBasicDtoConverter.convertFrom(list);
    }

    @GetMapping("/config/register-supported")
    public HttpEntity<Boolean> registerSupported() {
        boolean result = systemConfigService.registerSupported();
        return new HttpEntity<>(result);
    }

    @GetMapping("/config/modify-account-supported")
    public HttpEntity<Boolean> modifyAccountSupported() {
        boolean result = systemConfigService.modifyAccountSupported();
        return new HttpEntity<>(result);
    }

}
