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

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;

import java.util.Date;

@Data
@NoArgsConstructor
public class SimpleQuestionDto {

    private Long id;

    private Long vid;

    private String content;

    private String answer;

    private Date answerEditTime;

    public SimpleQuestionDto(QuestionNode questionNode) {
        this.id = questionNode.getQuestionId();
        this.vid = questionNode.getQuestionVid();
        QuestionVersion qv = questionNode.getQuestionVersion();
        if (qv != null) {
            this.content = qv.getContent();
            this.answer = qv.getAnswer();
            this.answerEditTime = qv.getAnswerEditTime();
        }

    }
}
