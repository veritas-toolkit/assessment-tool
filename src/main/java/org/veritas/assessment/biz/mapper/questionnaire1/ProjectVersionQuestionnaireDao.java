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
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectVersionQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectVersionQuestionnaire;

import java.util.Collections;
import java.util.List;

@Repository
@Slf4j
@Deprecated
public class ProjectVersionQuestionnaireDao
        extends QuestionnaireValueDao<ProjectVersionQuestion, ProjectVersionQuestionnaire> {

    @Autowired
    private ProjectVersionQuestionMapper questionMapper;

    @Autowired
    private ProjectVersionQuestionnaireMapper questionnaireMapper;

    @Override
    protected QuestionnaireValueMapper<ProjectVersionQuestionnaire> getQuestionnaireMapper() {
        return questionnaireMapper;
    }

    @Override
    protected QuestionValueMapper<ProjectVersionQuestion> getQuestionMapper() {
        return questionMapper;
    }

    public List<ProjectVersionQuestionnaire> findByProjectId(Integer projectId) {
        List<ProjectVersionQuestionnaire> list = questionnaireMapper.findByProjectId(projectId);
        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }
        for (ProjectVersionQuestionnaire questionnaire : list) {
            List<ProjectVersionQuestion> questionList =
                    questionMapper.findByQuestionnaireId(questionnaire.questionnaireId());
            questionnaire.addQuestions(questionList);
        }
        return list;
    }


    public ProjectVersionQuestionnaire findLatestByProjectId(Integer projectId) {
        ProjectVersionQuestionnaire questionnaire = questionnaireMapper.findLatestByProjectId(projectId);
        if (questionnaire == null) {
            return null;
        }
        List<ProjectVersionQuestion> questionList =
                questionMapper.findByQuestionnaireId(questionnaire.questionnaireId());
        questionnaire.addQuestions(questionList);
        return questionnaire;
    }


    @Override
    public int addQuestion(Integer questionnaireId, ProjectVersionQuestion question) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int deleteQuestionById(Integer questionnaireId, Integer questionId) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int deleteQuestionById(Integer questionnaireId, List<Integer> questionIdList) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int addQuestionList(Integer questionnaireId, List<ProjectVersionQuestion> questionList) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int deleteSubQuestion(Integer questionnaireId, String part, Integer partSerial, Integer subSerial) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int deleteQuestionWithSub(Integer questionnaireId, String part, Integer partSerial) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int updateQuestion(Integer questionnaireId, List<ProjectVersionQuestion> questionList) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }

    @Override
    public int updateQuestionSerial(List<ProjectVersionQuestion> questionList) {
        throw new UnsupportedOperationException("Cannot modify the questionnaire version.");
    }
}
