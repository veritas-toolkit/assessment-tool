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

package org.veritas.assessment.biz.entity.artifact;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.JdbcType;
import org.veritas.assessment.biz.entity.jsonmodel.JsonModel;
import org.veritas.assessment.common.handler.TimestampHandler;

import java.util.Date;

@Data
@TableName(value = "vat_model_artifact", autoResultMap = true)
@Slf4j
public class ModelArtifact {
    @TableId(type = IdType.AUTO)
    private Integer versionId;

    private Integer projectId;

    private Integer uploadUserId;

    @TableField(typeHandler = TimestampHandler.class, jdbcType = JdbcType.VARCHAR)
    private Date uploadTime;

    private String filename;

    @JsonIgnoreProperties
    private String jsonZipPath;

    private String jsonContentSha256;

    @TableField(exist = false)
    @JsonIgnoreProperties
    private String jsonContent;

    @TableField(exist = false)
    @JsonIgnoreProperties
    private JsonModel jsonModel;
}
