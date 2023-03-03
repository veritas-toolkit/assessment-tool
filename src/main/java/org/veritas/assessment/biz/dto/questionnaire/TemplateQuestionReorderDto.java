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
import org.veritas.assessment.biz.constant.AssessmentStep;
import org.veritas.assessment.biz.constant.Principle;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TemplateQuestionReorderDto {
    @NotNull(message = "The templateId cannot be null.")
    private Integer templateId;

    @NotNull(message = "The principle cannot be null.")
    private Principle principle;

    @NotNull(message = "The step cannot be null.")
    private AssessmentStep step;

    private List<Long> questionIdList;
}
