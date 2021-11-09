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
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.handler.DateHandler;
import org.veritas.assessment.common.handler.TimestampHandler;
import org.veritas.assessment.system.constant.PermissionType;
import org.veritas.assessment.system.constant.ResourceType;
import org.veritas.assessment.system.constant.RoleType;
import org.veritas.assessment.system.entity.Group;

import java.util.Date;
import java.util.Objects;

@TableName(autoResultMap = true)
@Data
public class UserRole {
    private Integer userId;
    private ResourceType resourceType;
    private Integer resourceId;
    private Integer roleId;
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;
    @TableField(typeHandler = DateHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date expirationDate;

    @TableField(exist = false)
    private Role role;


    public boolean enable() {
        // TODO: 2021/6/30
        return true;
    }

    public boolean ofProject() {
        if (resourceType == null) {
            throw new IllegalStateException();
        }
        return resourceType == ResourceType.PROJECT;
    }

    public boolean ofGroup() {
        if (resourceType == null) {
            throw new IllegalStateException();
        }
        return resourceType == ResourceType.GROUP;
    }

    /**
     * Same project or the project belongs to the group.
     *
     * @param project the project to be checked.
     * @return true if the UserRole match the project, else return false.
     */
    public boolean match(Project project) {
        Objects.requireNonNull(project);
        if (resourceType == ResourceType.PROJECT && Objects.equals(resourceId, project.getId())) {
            return true;
        } else {
            return resourceType == ResourceType.GROUP && Objects.equals(resourceId, project.getGroupOwnerId());
        }
    }

    public boolean match(Group group) {
        Objects.requireNonNull(group);
        if (ResourceType.GROUP != resourceType) {
            return false;
        }
        return Objects.equals(resourceId, group.getId());
    }

    public boolean hasPermission(PermissionType permissionType) {
        Objects.requireNonNull(permissionType);
        if (role == null) {
            throw new IllegalStateException();
        }
        return role.hasPermission(permissionType);
    }

    public boolean hasPermission(Project project, PermissionType permissionType) {
        boolean hasCommonPermission = this.match(project) && this.hasPermission(permissionType);
        if (hasCommonPermission) {
            return true;
        }
        boolean isCreatePersonProject = project.isPersonProject() && permissionType == PermissionType.PROJECT_CREATE;
        return isCreatePersonProject;
    }

    public boolean hasPermission(Group group, PermissionType permissionType) {
        return this.match(group) && this.hasPermission(permissionType);
    }

    public RoleType roleType() {
        if (role == null) {
            return null;
        }
        String roleName = role.getName();
        return RoleType.fromName(roleName);
    }

}
