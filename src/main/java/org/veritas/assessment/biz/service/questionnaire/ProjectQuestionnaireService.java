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

import org.veritas.assessment.biz.entity.QuestionCommentReadLog;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.QuestionComment;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;

import java.util.List;
import java.util.Map;

public interface ProjectQuestionnaireService extends BaseQuestionnaireService<ProjectQuestion, ProjectQuestionnaire> {

    default ProjectQuestionnaire findByProject(Integer projectId) {
        return findQuestionnaireById(projectId);
    }

    // create questionnaire from a template
    ProjectQuestionnaire create(Integer projectId, Integer templateId);

    // edit answer
    ProjectQuestion editAnswer(Integer projectId, ProjectQuestion projectQuestion);

    // force update the answers to the main question with all sub questions.
    void forceUpdateAnswerList(Integer projectId, List<ProjectQuestion> mainQuestionList);


    // comment
    int addComment(QuestionComment comment);

    // find comment list question
    List<QuestionComment> findCommentListByQuestionId(Integer questionId);

    // find comment list by project
    List<QuestionComment> findCommentListByProjectId(Integer projectId);

    Map<Integer, QuestionCommentReadLog> findCommentReadLog(Integer userId, Integer projectId);

    void updateCommentReadLog(Integer userId, Integer projectId, Integer questionId, Integer commentId);
}
