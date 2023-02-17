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
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;

import java.util.Date;

@Data
@NoArgsConstructor
public class ModelArtifactDto {
    Integer projectId;

    Integer uploadUserId;

    Date uploadTime;

    String filename;

    String jsonContentSha256;

    public ModelArtifactDto(ModelArtifact modelArtifact) {
        this.projectId = modelArtifact.getProjectId();
        this.filename = modelArtifact.getFilename();
        this.uploadUserId = modelArtifact.getUploadUserId();
        this.uploadTime = modelArtifact.getUploadTime();
        this.setJsonContentSha256(modelArtifact.getJsonContentSha256());
    }
}
