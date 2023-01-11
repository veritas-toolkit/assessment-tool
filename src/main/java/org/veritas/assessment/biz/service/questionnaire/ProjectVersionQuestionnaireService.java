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

import org.veritas.assessment.biz.entity.questionnaire1.ProjectVersionQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectVersionQuestionnaire;

import java.util.List;

public interface ProjectVersionQuestionnaireService
        extends BaseQuestionnaireService<ProjectVersionQuestion, ProjectVersionQuestionnaire> {

    default ProjectVersionQuestionnaire findByVersionId(Integer versionId) {
        return findQuestionnaireById(versionId);
    }

    // create questionnaire from a template
    ProjectVersionQuestionnaire create(Integer projectId);

    List<ProjectVersionQuestionnaire> findListByProjectId(Integer projectId);


}
