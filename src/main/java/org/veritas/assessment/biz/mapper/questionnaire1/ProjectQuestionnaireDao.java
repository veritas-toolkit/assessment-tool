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

package org.veritas.assessment.biz.mapper.questionnaire1;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.util.List;
import java.util.Objects;

@Repository
@Slf4j
@CacheConfig(cacheNames = "project_questionnaire")
@Deprecated
public class ProjectQuestionnaireDao extends QuestionnaireValueDao<ProjectQuestion, ProjectQuestionnaire> {
    @Autowired
    private ProjectQuestionMapper projectQuestionMapper;

    @Autowired
    private ProjectQuestionnaireMapper projectQuestionnaireMapper;

    @Override
    protected QuestionnaireValueMapper<ProjectQuestionnaire> getQuestionnaireMapper() {
        return projectQuestionnaireMapper;
    }

    @Override
    protected QuestionValueMapper<ProjectQuestion> getQuestionMapper() {
        return projectQuestionMapper;
    }

    @Cacheable(key = "#projectId")
    public ProjectQuestionnaire findByProjectId(Integer projectId) {
        return this.findQuestionnaire(projectId);
    }

    // edit answer
    @CacheEvict(key = "#projectId")
    public int editAnswer(Integer projectId, List<ProjectQuestion> questionList) {
        Objects.requireNonNull(projectId);
        if (questionList == null || questionList.isEmpty()) {
            return 0;
        }
        for (ProjectQuestion question : questionList) {
            if (!Objects.equals(projectId, question.questionnaireId())) {
                throw new IllegalArgumentException(
                        "Arg[questionnaireId] should be same as [questionList[*].questionnaire]");
            }
        }

        int result = projectQuestionMapper.updateAnswerList(questionList);
        if (result != questionList.size()) {
            log.warn("Update result[{}] is not equal to the arg list size[{}. Some database error may happened.",
                    result, questionList.size());
        }
        return result;
    }

}
