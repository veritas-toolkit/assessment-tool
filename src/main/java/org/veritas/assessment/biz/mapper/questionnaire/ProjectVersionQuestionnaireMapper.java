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

package org.veritas.assessment.biz.mapper.questionnaire;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.ProjectVersionQuestionnaire;

import java.util.List;

@Repository
@Deprecated
public interface ProjectVersionQuestionnaireMapper extends QuestionnaireValueMapper<ProjectVersionQuestionnaire> {


    default List<ProjectVersionQuestionnaire> findByProjectId(Integer projectId) {
        LambdaQueryWrapper<ProjectVersionQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectVersionQuestionnaire::getProjectId, projectId);
        wrapper.orderByDesc(ProjectVersionQuestionnaire::getVersionId);
        return selectList(wrapper);
    }

//    @Select("select max(version_id) from vat_project_version_questionnaire where projectId = #{projectId}")
//    Integer maxVersionId(@Param("projectId") Integer projectId);

    default ProjectVersionQuestionnaire findLatestByProjectId(Integer projectId) {
        LambdaQueryWrapper<ProjectVersionQuestionnaire> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectVersionQuestionnaire::getProjectId, projectId);
        wrapper.orderByDesc(ProjectVersionQuestionnaire::getVersionId);
        wrapper.last("limit 1");
        return selectOne(wrapper);
    }

}
