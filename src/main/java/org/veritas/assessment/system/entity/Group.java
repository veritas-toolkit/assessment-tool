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

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;

/**
 * User group.
 */
@Data
@TableName(autoResultMap = true)
public class Group {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private Integer creatorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date lastModifiedTime;
    /**
     * logic delete field flag.
     */
    @TableLogic
    @JsonIgnore
    private boolean deleted = false;

    /**
     * delete time
     */
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    @JsonIgnore
    private Date deleteTime = null;


    public String identification() {
        return getId() + ":" + getName();
    }
}
