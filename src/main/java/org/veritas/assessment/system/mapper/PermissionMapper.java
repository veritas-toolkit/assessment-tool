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

package org.veritas.assessment.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.veritas.assessment.system.rbac.Permission;

import java.util.List;

interface PermissionMapper extends BaseMapper<Permission> {

    @Select("select p.id, p.resource_type, p.name, p.description " +
            "from vat2_permission p " +
            "left join vat2_role_permission rp on rp.permission_id = p.id and rp.role_id = #{roleId}" +
            "where rp.role_id = #{roleId} " +
            "order by p.id")
    List<Permission> selectByRoleId(@Param("roleId") int roleId);
}
