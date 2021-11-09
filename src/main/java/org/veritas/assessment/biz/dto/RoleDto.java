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
import lombok.NoArgsConstructor;
import org.veritas.assessment.system.rbac.Role;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class RoleDto {

    String name;

    List<String> permissionList;

    public RoleDto(Role role) {
        this.name = role.getName();
        this.permissionList = role.getPermissionList().stream()
                .map(permission -> permission.getResourceType() + ":" + permission.getName())
                .collect(Collectors.toList());
        //                .map(Permission::getName)
//                .collect(Collectors.toList());;

    }
}
