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

package org.veritas.assessment.biz.service.questionnaire1.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestion;
import org.veritas.assessment.biz.entity.questionnaire1.ProjectQuestionnaire;
import org.veritas.assessment.biz.entity.questionnaire1.QuestionValue;
import org.veritas.assessment.biz.entity.questionnaire1.TemplateQuestionnaire;
import org.veritas.assessment.biz.mapper.QuestionCommentMapper;
import org.veritas.assessment.biz.mapper.QuestionCommentReadLogMapper;
import org.veritas.assessment.biz.mapper.questionnaire1.ProjectQuestionnaireDao;
import org.veritas.assessment.biz.service.questionnaire1.AbstractQuestionnaireService1;
import org.veritas.assessment.biz.service.questionnaire1.ProjectQuestionnaireService1;
import org.veritas.assessment.biz.service.questionnaire1.TemplateQuestionnaireService1;
import org.veritas.assessment.common.exception.ErrorParamException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@Deprecated
public class ProjectQuestionnaireServiceImpl1
        extends AbstractQuestionnaireService1<ProjectQuestion, ProjectQuestionnaire, ProjectQuestionnaireDao>
        implements ProjectQuestionnaireService1 {

    @Autowired
    private ProjectQuestionnaireDao dao;

    @Autowired
    private TemplateQuestionnaireService1 templateQuestionnaireService1;

    @Autowired
    private QuestionCommentMapper commentMapper;

    @Autowired
    private QuestionCommentReadLogMapper commentReadLogMapper;

    @Override
    @Transactional
    public ProjectQuestionnaire create(Integer projectId, Integer templateId) {
        TemplateQuestionnaire template = templateQuestionnaireService1.findQuestionnaireById(templateId);
        if (template == null) {
            throw new ErrorParamException("The questionnaire template does not exist.");
        }
        ProjectQuestionnaire questionnaire = new ProjectQuestionnaire();
        questionnaire.copyFrom(template, ProjectQuestion::new);
        questionnaire.configQuestionnaireIdWithQuestion(projectId);
        return this.add(questionnaire);
    }

    @Override
    @Transactional
    public ProjectQuestion editAnswer(Integer projectId, ProjectQuestion mainQuestion) {
        Objects.requireNonNull(projectId, "Arg[projectId] should not be null.");
        Objects.requireNonNull(mainQuestion, "Arg[mainQuestion] should not be null.");
        if (mainQuestion.getId() == null) {
            throw new ErrorParamException("Cannot find the question without id.");
        }
        ProjectQuestionnaire questionnaire = dao.findByProjectId(projectId);
        if (questionnaire == null) {
            throw new ErrorParamException("");
        }
        ProjectQuestion originMain = questionnaire.findQuestionById(mainQuestion.getId());
        if (originMain == null || !originMain.isMainQuestion()) {
            throw new ErrorParamException("");
        }
        mainQuestion.configureProperty(originMain.questionnaireId(), originMain.getPart(), originMain.getPartSerial());

        List<ProjectQuestion> originList = originMain.toList();
        Map<Integer, ProjectQuestion> originMap =
                originList.stream().collect(Collectors.toMap(QuestionValue::getId, e -> e));

        List<ProjectQuestion> newList = mainQuestion.toList();

        Date now = new Date();
        List<ProjectQuestion> answerEditedQuestionList = new ArrayList<>();
        for (ProjectQuestion question : newList) {
            if (question.getId() == null) {
                throw new ErrorParamException("");
            }
            ProjectQuestion origin = originMap.get(question.getId());
            if (origin == null) {
                throw new ErrorParamException();
            }
            boolean edited = !StringUtils.equals(origin.getAnswer(), question.getAnswer());
            if (edited) {
                question.setEditTime(now);
                question.setProjectId(origin.getProjectId());
                question.setPart(origin.getPart());
                question.setPartSerial(origin.getPartSerial());
                question.setSubSerial(origin.getSubSerial());
                answerEditedQuestionList.add(question);
            }
        }
        if (!answerEditedQuestionList.isEmpty()) {
            if (!answerEditedQuestionList.contains(mainQuestion)) {
                answerEditedQuestionList.add(mainQuestion);
            }
        }
        if (!answerEditedQuestionList.isEmpty()) {
            dao.editAnswer(projectId, answerEditedQuestionList);
            questionnaire = dao.findByProjectId(projectId);
            return questionnaire.findQuestionById(mainQuestion.getId());
        } else {
            return originMain;
        }
    }

    @Override
    @Transactional
    public void forceUpdateAnswerList(Integer projectId, List<ProjectQuestion> mainQuestionList) {
        if (mainQuestionList == null || mainQuestionList.isEmpty()) {
            return;
        }
        for (ProjectQuestion question : mainQuestionList) {
            List<ProjectQuestion> newList = question.toList();
            dao.editAnswer(projectId, newList);
        }
    }


}