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
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;

import java.util.Objects;

@Repository
public interface ModelArtifactMapper extends BaseMapper<ModelArtifact> {

    default int add(ModelArtifact modelArtifact) {
        return insert(modelArtifact);
    }

    default ModelArtifact findByProjectId(int projectId) {
        LambdaQueryWrapper<ModelArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifact::getProjectId, projectId);
        wrapper.orderByDesc(ModelArtifact::getVersionId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

    default ModelArtifact findByVersionId(Integer versionId) {
        Objects.requireNonNull(versionId);
        LambdaQueryWrapper<ModelArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifact::getVersionId, versionId);
        return selectOne(wrapper);
    }

    default ModelArtifact findLatest(Integer projectId) {
        Objects.requireNonNull(projectId);
        LambdaQueryWrapper<ModelArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifact::getProjectId, projectId);
        wrapper.orderByDesc(ModelArtifact::getVersionId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }
}
