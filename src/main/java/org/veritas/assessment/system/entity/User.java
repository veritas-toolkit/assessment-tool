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
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.io.Serializable;
import java.util.Date;


@Data
@ToString(exclude = {"password"})
@TableName(autoResultMap = true)
public class User implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * username for login
     */
    private String username;
    /**
     * full name for display
     */
    private String fullName;
    /**
     * email of user. email can be used to sign in.
     */
    private String email;
    /**
     * login password
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    /**
     * The account has 'admin' role or not.
     */
    private boolean admin = false;
    /**
     * Times of fail login attempt. If fail login attempt is to much, the account should be locked.
     */
    private Integer loginAttempt;
    /**
     * number of project witch user can create.
     */
    private Integer projectLimited;
    /**
     * number of group witch user can create.
     */
    private Integer groupLimited;
    /**
     * Created time of the user.
     */
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date createdTime;
    /**
     * Last login time of the user.
     */
    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date lastLoginTime;

    private boolean finishedUserGuide = false;
    /**
     * The account is locked.
     */
    private boolean locked = false;

    /**
     * The user should change the password after next login.
     */
    private boolean shouldChangePassword = true;

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

    public void loginAttemptIncrease() {
        if (this.getLoginAttempt() == null) {
            this.setLoginAttempt(1);
        } else {
            this.setLoginAttempt(this.getLoginAttempt() + 1);
        }
    }

    public void loginSuccess() {
        this.setLastLoginTime(new Date());
    }

    public String identification() {
        return getId() + ":" + getUsername() + ":" + getFullName();
    }
}
