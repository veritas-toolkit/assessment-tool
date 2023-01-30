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

package org.veritas.assessment.biz.service.questionnaire1;

import org.veritas.assessment.biz.entity.questionnaire1.QuestionValue;
import org.veritas.assessment.biz.entity.questionnaire1.QuestionnaireValue;

@Deprecated
public interface BaseQuestionnaireService1<T extends QuestionValue<T>, Q extends QuestionnaireValue<T>> {

    Q findQuestionnaireById(Integer questionnaireId);

    Q add(Q questionnaire);

    T addMainQuestion(Integer questionnaireId, T mainQuestion);

    int deleteMainQuestion(Integer questionnaireId, Integer mainQuestionId);

    int deleteSubQuestion(Integer questionnaireId, Integer subQuestionId);

    T updateMainQuestionWithSub(Integer questionnaireId, T mainQuestion);

}
