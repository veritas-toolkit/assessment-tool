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

package org.veritas.assessment.biz.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

public enum QuestionType {
    MAIN_QUESTION(1),
    SUB_QUESTION(2);

    @Getter
    @EnumValue
    private final int code;

    QuestionType(int code) {
        this.code = code;
    }
}
