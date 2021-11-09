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

package org.veritas.assessment.biz.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.artifact.ModelArtifactVersion;

import java.util.Objects;

@Repository
public interface ModelArtifactVersionMapper extends BaseMapper<ModelArtifactVersion> {

    default int add(ModelArtifactVersion modelArtifactVersion) {
        return insert(modelArtifactVersion);
    }

    default ModelArtifactVersion findByVersionId(Integer versionId) {
        Objects.requireNonNull(versionId);
        LambdaQueryWrapper<ModelArtifactVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifactVersion::getVersionId, versionId);
        return selectOne(wrapper);
    }

    default ModelArtifactVersion findLatest(Integer projectId) {
        Objects.requireNonNull(projectId);
        LambdaQueryWrapper<ModelArtifactVersion> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifactVersion::getProjectId, projectId);
        wrapper.orderByDesc(ModelArtifactVersion::getVersionId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }
}
