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

import lombok.Data;
import org.veritas.assessment.biz.dto.UserSimpleDto;

import java.util.Date;
import java.util.List;

@Data
public class Question2Dto {

    private int id;

    private int vid;

    private String serial;

    private String content;

    private String answer;

    private Date answerEditTime;

    private UserSimpleDto answerEditUser;

    private List<SubQuestionDto> subQuestionList;

}
