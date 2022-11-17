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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.ProjectReport;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
public interface ProjectReportMapper extends BaseMapper<ProjectReport> {

    @CacheEvict(key = "#projectReport.projectId", cacheNames = "project_report_list")
    default int add(ProjectReport projectReport) {
        Objects.requireNonNull(projectReport);
        if (projectReport.getCreatedTime() == null) {
            projectReport.setCreatedTime(new Date());
        }
        LambdaQueryWrapper<ProjectReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectReport::getProjectId, projectReport.getProjectId());
        long count = selectCount(wrapper);
        long nextVersion = count + 1;
        if (nextVersion > Integer.MAX_VALUE) {
            throw new RuntimeException();
        }
        projectReport.setVersionIdOfProject((int) nextVersion);
        return insert(projectReport);
    }

    @Cacheable(cacheNames = "project_report",
            key = "'p_'+#projectId+'_v_'+#projectVersionId",
            condition = "#result != null")
    default ProjectReport findPidAndVid(Integer projectId, Integer projectVersionId) {
        LambdaQueryWrapper<ProjectReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectReport::getProjectId, projectId);
        wrapper.eq(ProjectReport::getVersionIdOfProject, projectVersionId);
        return selectOne(wrapper);
    }

    @Cacheable(key = "#projectId", cacheNames = "project_report_list")
    default List<ProjectReport> findAllByProjectId(Integer projectId) {
        LambdaQueryWrapper<ProjectReport> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectReport::getProjectId, projectId);
        wrapper.orderByDesc(ProjectReport::getVersionIdOfProject);
        return selectList(wrapper);
    }
}
