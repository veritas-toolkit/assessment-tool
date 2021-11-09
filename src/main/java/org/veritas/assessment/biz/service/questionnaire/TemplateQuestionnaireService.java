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

package org.veritas.assessment.biz.service.questionnaire;

import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestion;
import org.veritas.assessment.biz.entity.questionnaire.TemplateQuestionnaire;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.List;

public interface TemplateQuestionnaireService extends BaseQuestionnaireService<TemplateQuestion, TemplateQuestionnaire> {

    void delete(Integer templateId);

    List<TemplateQuestionnaire> findTemplateList();

    Pageable<TemplateQuestionnaire> findTemplatePageable(String prefix, String keyword, int page, int pageSize);

    // create a new template from an existing template
    TemplateQuestionnaire create(Integer fromTemplateId, String name, String description);

    TemplateQuestionnaire updateBasicInfo(Integer templateId, String name, String description);
}
