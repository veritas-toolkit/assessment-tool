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

package org.veritas.assessment.biz.dto.questionnaire;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;
import org.veritas.assessment.biz.util.HtmlCompare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
public class QuestionDiffDto {

    private QuestionRecordDto basedQuestion;

    private QuestionRecordDto newQuestion;

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
            this.compareTwoVersions(basedQuestionNode, newQuestionNode);
        }
        this.doCompare();
    }

    private void doCompare() {
        String basedQ = basedQuestion != null ? basedQuestion.getQuestion() : "";
        String newQ = newQuestion != null ? newQuestion.getQuestion() : "";

        if (basedQuestion == null || newQuestion == null || !basedQuestion.sameSerial(newQuestion)) {
            basedQ = basedQuestion != null ? basedQuestion.questionContentForCompare() : "";
            newQ = newQuestion != null ? newQuestion.questionContentForCompare() : "";
        }

        HtmlCompare questionHtmlCompare = new HtmlCompare(basedQ, newQ);
        if (basedQuestion != null) {
            basedQuestion.setQuestionHtml(questionHtmlCompare.comparedBasedHtml());
            if (log.isTraceEnabled()) {
                log.trace("based question html:\n{}", basedQuestion.getQuestionHtml());
            }
        }
        if (newQuestion != null) {
            newQuestion.setQuestionHtml(questionHtmlCompare.comparedNewHtml());
            if (log.isTraceEnabled()) {
                log.trace("new question html:\n{}", newQuestion.getQuestionHtml());
            }
        }

        String basedAns = basedQuestion != null ? basedQuestion.getAnswer() : "";
        String newAns = newQuestion != null ? newQuestion.getAnswer() : "";
        HtmlCompare answerCompare = new HtmlCompare(basedAns, newAns);

        if (basedQuestion != null) {
            basedQuestion.setAnswerHtml(answerCompare.comparedBasedHtml());
            if (log.isTraceEnabled()) {
                log.info("based answer html:\n{}", basedQuestion.getAnswerHtml());
            }
        }
        if (newQuestion != null) {
            newQuestion.setAnswerHtml(answerCompare.comparedNewHtml());
            if (log.isTraceEnabled()) {
                log.info("new answer html:\n{}", newQuestion.getAnswerHtml());
            }
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

    private void compareTwoVersions(QuestionNode basedQuestionNode, QuestionNode newQuestionNode) {
        boolean sameQuestion = Objects.equals(basedQuestionNode.getQuestionId(), newQuestionNode.getQuestionId());
        if (!sameQuestion) {
            throw new IllegalArgumentException();
        }
        this.basedQuestion = new QuestionRecordDto(basedQuestionNode);
        this.newQuestion = new QuestionRecordDto(newQuestionNode);
//        this.diff = !Objects.equals(basedQuestionNode.getQuestionVid(), newQuestionNode.getQuestionVid());
        this.diff = basedQuestionNode.isSame(newQuestionNode);

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
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        List<QuestionDiffDto> allSubs = new ArrayList<>(subsInNew);
        allSubs.addAll(subsOnlyInBased);
        this.subList = Collections.unmodifiableList(allSubs);
    }

    @Data
    public static class QuestionRecordDto {
        private Long questionId;

        private Long questionVid;

        private String serial;

        private Integer subSerial;

        private String question;

//        private String questionHtml = "<span> Hello word. <span class='add'> some thing </span> <span class='del'> delete somme thing</span> common</span>";
        private String questionHtml;

        private Date questionEditTime;

        private UserSimpleDto questionEditUser;

        private String answer;
//        private String answerHtml = "<span> Hello word. <span class='add'> some thing </span> <span class='del'> delete somme thing</span> common</span>";
        private String answerHtml;

        private Date answerEditTime;

        private UserSimpleDto answerEditUser;


        public QuestionRecordDto(QuestionNode questionNode) {
            QuestionVersion questionVersion = questionNode.getQuestionVersion();
            this.questionId = questionNode.getQuestionId();
            this.questionVid = questionVersion.getVid();
            this.serial = questionNode.serial();
            this.subSerial = questionNode.getSubSerial();
            this.question = questionVersion.getContent();
            this.questionEditTime = questionVersion.getContentEditTime();
            this.answer = questionVersion.getAnswer();
            this.answerEditTime = questionVersion.getAnswerEditTime();
        }

        private String questionContentForCompare() {
            if (this.subSerial == 0) {
                return this.serial + ". " + this.question;
            } else {
                return this.subSerial + ". " + this.question;
            }
        }
        private boolean sameSerial(QuestionRecordDto other) {
            if (other == null) {
                return false;
            }
            boolean sameSerial = StringUtils.equals(this.getSerial(), other.getSerial());
            boolean sameSubSerial = Objects.equals(this.getSubSerial(), other.getSubSerial());
            return sameSerial && sameSubSerial;
        }

    }
}
