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

package org.veritas.assessment.biz.entity.questionnaire;

import java.util.List;

public class ProjectQuestionnaireTest {
    public static ProjectQuestionnaire create() {
        return create(1);

    }

    public static ProjectQuestionnaire create(Integer projectId) {
        List<ProjectQuestion> questionList = ProjectQuestionTest.data(projectId);
        ProjectQuestionnaire questionnaire = new ProjectQuestionnaire();
        questionnaire.setProjectId(projectId);
        questionnaire.addQuestions(questionList);
        questionnaire.setPartATitle("title A");
        questionnaire.setPartBTitle("title B");
        questionnaire.setPartCTitle("title C");
        questionnaire.setPartDTitle("title D");
        questionnaire.setPartETitle("title E");

        return questionnaire;
    }

}