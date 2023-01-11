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

package org.veritas.assessment.biz.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.util.List;
import java.util.Objects;

/**
 * The Project questionnaire completion progress.
 */
@Data
@NoArgsConstructor
public class QuestionnaireProgressDto {
    private Integer completed;

    private Integer count;

    public QuestionnaireProgressDto(ProjectQuestionnaire questionnaire) {
        Objects.requireNonNull(questionnaire);
        Objects.requireNonNull(questionnaire.getQuestions());
        List<ProjectQuestion> list = questionnaire.getQuestions();
        this.setCount(list.size());
        int completedCount = 0;
        for (ProjectQuestion question : list) {
            if (question.completed()) {
                ++completedCount;
            }
        }
        this.setCompleted(completedCount);
    }
}
