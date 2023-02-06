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

package org.veritas.assessment.biz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.springframework.security.core.parameters.P;
import org.veritas.assessment.biz.constant.Principle;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@TableName(autoResultMap = true)
@Slf4j
public class Project {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private String name;

    private String description;

    private Integer userOwnerId;

    private Integer groupOwnerId;

    private Integer businessScenario;

    @TableField(value = "principle_g")
    private boolean principleGeneric = DEFAULT_PRINCIPLE_GENERIC;

    @TableField(value = "principle_f")
    private boolean principleFairness;

    @TableField(value = "principle_ea")
    private boolean principleEA;

    @TableField(value = "principle_t")
    private boolean principleTransparency;

    private Integer currentModelArtifactVid;

    private Long currentQuestionnaireVid;

    private Integer creatorUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date lastEditedTime;

    private boolean archived = DEFAULT_ARCHIVED;

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

    private static final boolean DEFAULT_PRINCIPLE_GENERIC = true;

    private static final boolean DEFAULT_ARCHIVED = false;

    @JsonIgnore
    public boolean isPersonProject() {
        return userOwnerId != null;
    }

    @JsonIgnore
    public boolean isGroupProject() {
        return groupOwnerId != null;
    }

    public void setUserOwnerId(Integer userOwnerId) {
        if (userOwnerId == null) {
            this.userOwnerId = null;
        } else {
            if (groupOwnerId != null) {
                log.warn("groupOwnerId is [{}], and set to null.", groupOwnerId);
                groupOwnerId = null;
            }
            this.userOwnerId = userOwnerId;
        }
    }

    public void setGroupOwnerId(Integer groupOwnerId) {
        if (groupOwnerId == null) {
            this.groupOwnerId = null;
        } else {
            if (userOwnerId != null) {
                log.warn("userOwnerId is [{}], and set to null.", userOwnerId);
                userOwnerId = null;
            }
            this.groupOwnerId = groupOwnerId;
        }
    }

    @JsonIgnore
    public Integer getOwnerId() {
        if (groupOwnerId != null) {
            return groupOwnerId;
        } else if (userOwnerId != null) {
            return userOwnerId;
        } else {
            throw new IllegalStateException();
        }
    }

    public List<Principle> principles() {
        List<Principle> list = new ArrayList<>();
        if (this.principleGeneric) {
            list.add(Principle.G);
        }
        if (this.principleFairness) {
            list.add(Principle.F);
        }
        if (this.principleEA) {
            list.add(Principle.EA);
        }
        if (this.principleTransparency) {
            list.add(Principle.T);
        }
        return Collections.unmodifiableList(list);
    }
}