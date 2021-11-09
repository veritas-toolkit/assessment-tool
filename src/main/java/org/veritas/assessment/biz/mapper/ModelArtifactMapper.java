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
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.artifact.ModelArtifact;

import java.util.Date;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "modelArtifact")
public interface ModelArtifactMapper extends BaseMapper<ModelArtifact> {

    @CacheEvict(key = "#modelArtifact.getProjectId()")
    default int updateOrInsert(ModelArtifact modelArtifact) {
        Objects.requireNonNull(modelArtifact);
        Integer projectId = modelArtifact.getProjectId();
        Objects.requireNonNull(projectId);
        if (modelArtifact.getUploadTime() == null) {
            modelArtifact.setUploadTime(new Date());
        }
        LambdaQueryWrapper<ModelArtifact> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ModelArtifact::getProjectId, projectId);
        int count = selectCount(queryWrapper);
        if (count <= 0) {
            return insert(modelArtifact);
        } else {
            return updateById(modelArtifact);
        }
    }

    @Cacheable(key = "#projectId", unless = "#result==null")
    default ModelArtifact findByProjectId(Integer projectId) {
        Objects.requireNonNull(projectId);
        LambdaQueryWrapper<ModelArtifact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ModelArtifact::getProjectId, projectId);
        return selectOne(wrapper);
    }
}
