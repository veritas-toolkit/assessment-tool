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
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.biz.dto.UserSimpleDto;
import org.veritas.assessment.biz.entity.questionnaire.QuestionNode;
import org.veritas.assessment.biz.entity.questionnaire.QuestionVersion;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class QuestionDto {

    private Long id;

    private Long vid;

    private String serial;

    private String question;

    private Date questionEditTime;

    private UserSimpleDto questionEditUser;

    private String answer;

    private Date answerEditTime;

    private UserSimpleDto answerEditUser;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<QuestionDto> subQuestionList;

//    public QuestionDto(QuestionNode questionNode, Map<Integer, UserSimpleDto> collaborators) {
    public QuestionDto(QuestionNode questionNode) {
        QuestionVersion questionVersion = questionNode.getQuestionVersion();
        this.id = questionNode.getQuestionId();
        this.vid = questionVersion.getVid();
        this.serial = questionNode.serial();
        this.question = questionVersion.getContent();
        this.questionEditTime =  questionVersion.getContentEditTime();
//        this.questionEditUser = questionEditUser;
        this.answer = questionVersion.getAnswer();
        this.answerEditTime = questionVersion.getAnswerEditTime();
//        this.answerEditUser = answerEditUser;

        if (StringUtils.isEmpty(this.answer)) {
            this.answer = "<div>hello</div>\n" +
                    "<img src=\"api/project/2/image/1.png\"/>\n" +
                    "<div>hello</div>";
        }

        List<QuestionNode> subList = questionNode.getSubList();
        if (subList != null) {
            this.subQuestionList = subList.stream().map(QuestionDto::new).collect(Collectors.toList());
        }
    }
}
