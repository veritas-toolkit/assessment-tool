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
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@EqualsAndHashCode(callSuper = true)
@Data
@TableName(autoResultMap = true)
@Slf4j
public class ModelArtifactVersion extends ModelArtifactValue {
    @TableId(type = IdType.AUTO)
    private Integer versionId;

    private Integer projectId;

    public static ModelArtifactVersion copyFrom(ModelArtifact modelArtifact) {
        Objects.requireNonNull(modelArtifact, "The arg[modelArtifact] should not be null.");
        ModelArtifactVersion version = new ModelArtifactVersion();
        version.setProjectId(modelArtifact.getProjectId());
        version.setUploadUserId(modelArtifact.getUploadUserId());
        version.setUploadTime(modelArtifact.getUploadTime());
        version.setFilename(modelArtifact.getFilename());
        version.setJsonZipPath(modelArtifact.getJsonZipPath());
        version.setJsonContentSha256(modelArtifact.getJsonContentSha256());
        version.setJsonContent(modelArtifact.getJsonContent());
        version.setJsonModel(modelArtifact.getJsonModel());
        return version;
    }
}
