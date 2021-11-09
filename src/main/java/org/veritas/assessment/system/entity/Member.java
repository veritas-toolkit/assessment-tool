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

package org.veritas.assessment.system.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.rbac.Role;
import org.veritas.assessment.system.rbac.UserRole;

import java.util.Date;
import java.util.Objects;

@Data
@NoArgsConstructor
public class Member {
    private Integer userId;
    private String username;
    private String fullName;
    private String email;
    private RoleType type;
    private Date joinTime;
    private Date expirationDate;
    private String roleName;
    private Integer roleId;
    /** The member's organization type: group or project */
    private String organizationType;

    public Member(User user, UserRole userRole) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(userRole);
        if (!Objects.equals(user.getId(), userRole.getUserId())) {
            throw new IllegalArgumentException(
                    String.format("Arg user.id[%d] and userRole.userId[%d] are not the same.",
                            user.getId(), userRole.getUserId()));
        }
        this.setUserId(user.getId());
        this.setUsername(user.getUsername());
        this.setEmail(user.getEmail());
        this.setFullName(user.getFullName());

        this.setType(RoleType.fromName(userRole.getRole().getName()));

        this.setJoinTime(userRole.getCreatedTime());
        this.setExpirationDate(userRole.getExpirationDate());
        this.setRoleId(userRole.getRoleId());

        Role role = userRole.getRole();
        if (role != null) {
            this.setRoleName(userRole.getRole().getName());

        }

        ResourceType resourceType = userRole.getResourceType();
        if (resourceType != null) {
            this.setOrganizationType(resourceType.getTypeName());
        }
    }
}
