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

package org.veritas.assessment.biz.dto.v2.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
public class QuestionDiffDto {

    private QuestionRecordDto basedQuestion;

    private QuestionRecordDto newQuestion;

    // only consider the question and answer, without subs.
    private boolean diff;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<QuestionDiffDto> subList;

    public QuestionDiffDto(QuestionNode basedQuestionNode, QuestionNode newQuestionNode) {
        if (basedQuestionNode == null && newQuestionNode == null) {
            throw new IllegalArgumentException("Both args are null.");
        } else if (newQuestionNode == null) {
            this.onlyBased(basedQuestionNode);
        } else if (basedQuestionNode == null) {
            this.onlyNew(newQuestionNode);
        } else {
            this.twoNode(basedQuestionNode, newQuestionNode);
        }
    }

    private void onlyBased(QuestionNode basedQuestionNode) {
        this.basedQuestion = new QuestionRecordDto(basedQuestionNode);
        this.diff = true;
        if (basedQuestionNode.isMain()) {
            this.subList = basedQuestionNode.getSubList().stream()
                    .map(n -> new QuestionDiffDto(n, null))
                    .collect(Collectors.toList());
        }
    }

    private void onlyNew(QuestionNode newQuestionNode) {
        this.newQuestion = new QuestionRecordDto(newQuestionNode);
        this.diff = true;
        if (newQuestionNode.isMain()) {
            this.subList = newQuestionNode.getSubList().stream()
                    .map(n -> new QuestionDiffDto(null, n))
                    .collect(Collectors.toList());
        }
    }

    private void twoNode(QuestionNode basedQuestionNode, QuestionNode newQuestionNode) {
        boolean sameQuestion = Objects.equals(basedQuestionNode.getQuestionId(), newQuestionNode.getQuestionId());
        if (!sameQuestion) {
            throw new IllegalArgumentException();
        }
        this.basedQuestion = new QuestionRecordDto(basedQuestionNode);
        this.newQuestion = new QuestionRecordDto(newQuestionNode);
        this.diff = !Objects.equals(basedQuestionNode.getQuestionVid(), newQuestionNode.getQuestionVid());
        if (basedQuestionNode.isSub()) {
            return;
        }
        List<QuestionNode> newSubList = newQuestionNode.getSubList();
        List<QuestionNode> basedSubList = basedQuestionNode.getSubList();
        List<QuestionDiffDto> subsInNew = newSubList.stream()
                .map(newNode -> {
                    QuestionNode basedNode = QuestionNode.findByQuestionId(basedSubList, newNode.getQuestionId());
                    return new QuestionDiffDto(basedNode, newNode);
                }).collect(Collectors.toList());

        List<QuestionDiffDto> subsOnlyInBased = basedSubList.stream()
                .map(basedNode -> {
                    QuestionNode newNode = QuestionNode.findByQuestionId(newSubList, basedNode.getQuestionId());
                    if (newNode == null) {
                        return new QuestionDiffDto(basedNode, null);
                    } else {
                        return null;
                    }
                }).collect(Collectors.toList());

        List<QuestionDiffDto> allSubs = new ArrayList<>(subsInNew);
        allSubs.addAll(subsOnlyInBased);
        this.subList = Collections.unmodifiableList(allSubs);
    }

    @Data
    public static class QuestionRecordDto {
        private Long id;

        private Long vid;

        private String serial;

        private String question;

        private Date questionEditTime;

        private UserSimpleDto questionEditUser;

        private String answer;

        private Date answerEditTime;

        private UserSimpleDto answerEditUser;

        public QuestionRecordDto(QuestionNode questionNode) {
            QuestionVersion questionVersion = questionNode.getQuestionVersion();
            this.id = questionNode.getQuestionId();
            this.vid = questionVersion.getVid();
            this.serial = questionNode.serial();
            this.question = questionVersion.getContent();
            this.questionEditTime = questionVersion.getContentEditTime();
            this.answer = questionVersion.getAnswer();
            this.answerEditTime = questionVersion.getAnswerEditTime();
        }
    }
}
