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

package org.veritas.assessment.biz.entity.questionnaire;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Data
@Deprecated
public class QuestionnairePartition<T extends QuestionValue<T>>
        implements Serializable {

    // A1-A5
    private String partName;

    private String partTitle;
    // part A1-A5
    private List<T> questionList;


    public static <T extends QuestionValue<T>, Q extends QuestionnaireValue<T>>
    List<QuestionnairePartition<T>> toPartitionList(Q questionnaire) {
        final List<String> partitionNameList = Arrays.asList("A", "B", "C", "D", "E");
        final List<Function<Q, String>> functionList = Arrays.asList(Q::getPartATitle, Q::getPartBTitle,
                Q::getPartCTitle, Q::getPartDTitle, Q::getPartETitle);

        List<QuestionnairePartition<T>> partList = new ArrayList<>(partitionNameList.size());
        for (int i = 0; i < partitionNameList.size(); i++) {
            String partName = partitionNameList.get(i);
            Function<Q, String> function = functionList.get(i);
            String partTitle = function.apply(questionnaire);

            QuestionnairePartition<T> part = new QuestionnairePartition<>();
            part.setPartName(partName);
            part.setPartTitle(partTitle);
            List<T> questionList = questionnaire.getQuestions()
                    .stream()
                    .filter(q -> StringUtils.equals(partName, q.getPart()))
                    .collect(Collectors.toList());
            part.setQuestionList(questionList);
            partList.add(part);
        }
        return Collections.unmodifiableList(partList);
    }
}
