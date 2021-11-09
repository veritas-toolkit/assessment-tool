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

package org.veritas.assessment.biz.dto;

import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.veritas.assessment.system.entity.User;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@Data
public class UserCreateDto {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]([._-](?![._-])|[a-zA-Z0-9]){3,28}[a-zA-Z0-9]$",
            message = "5-30 alphanumerics or characters(dot, underscore, hyphen), " +
                    "start and end with alphanumeric and characters cannot be consecutive")
    private String username;

    @NotBlank
    @Size(min = 5, max = 100, message = "5-100 alphanumeric or characters.")
    private String fullName;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private Boolean admin = false;

    @PositiveOrZero
    @NotNull
    private Integer projectLimited;

    @NotNull
    @PositiveOrZero
    private Integer groupLimited;

    public User toEntity() {
        User user = new User();
        BeanUtils.copyProperties(this, user, User.class);
        return user;
    }
}
