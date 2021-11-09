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

package org.veritas.assessment.system.rbac;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.veritas.assessment.system.constant.PermissionType;
import org.veritas.assessment.system.constant.RoleType;

import java.util.List;

@Data
@TableName(autoResultMap = true)
public class Role {
    private Integer id;
    private String type;
    private String name;
    private String description;
    @TableField(exist = false)
    private List<Permission> permissionList;

    public boolean hasPermission(String target, String action) {
        if (StringUtils.isEmpty(target)) {
            throw new IllegalArgumentException("target cannot be empty.");
        }
        if (StringUtils.isEmpty(action)) {
            throw new IllegalArgumentException("action cannot be empty.");
        }
        if (permissionList == null || permissionList.isEmpty()) {
            return false;
        }
        for (Permission auth : permissionList) {
            boolean sameTarget = StringUtils.equals(target, auth.getResourceType());
            boolean sameAction = StringUtils.equals(action, auth.getName());
            if (sameTarget && sameAction) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(PermissionType permissionType) {
        return hasPermission(permissionType.getTarget(), permissionType.getAction());
    }

    public boolean isTypeOf(RoleType roleType) {
        return StringUtils.equals(this.getName(), roleType.getTypeName());
    }
}