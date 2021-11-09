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
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.questionnaire.ProjectQuestionComment;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "project_question_comment")
public interface ProjectQuestionCommentMapper extends BaseMapper<ProjectQuestionComment> {

    @Caching(
            evict = {
                    @CacheEvict(key = "'q_' + #comment.questionId"),
                    @CacheEvict(key = "'p_' + #comment.projectId")
            }
    )
    default int add(ProjectQuestionComment comment) {
        Objects.requireNonNull(comment);
        if (comment.getCreatedTime() == null) {
            comment.setCreatedTime(new Date());
        }
        return insert(comment);
    }

    @Cacheable(key = "'q_' + #questionId", unless = "#result==null")
    default List<ProjectQuestionComment> findByQuestionId(Integer questionId) {
        Objects.requireNonNull(questionId);
        LambdaQueryWrapper<ProjectQuestionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectQuestionComment::getQuestionId, questionId);
        wrapper.orderByDesc(ProjectQuestionComment::getId);
        return Collections.unmodifiableList(selectList(wrapper));
    }

    @Cacheable(key = "'p_' + #projectId", unless = "#result==null")
    default List<ProjectQuestionComment> findByProjectId(Integer projectId) {
        Objects.requireNonNull(projectId);
        LambdaQueryWrapper<ProjectQuestionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProjectQuestionComment::getProjectId, projectId);
        wrapper.orderByAsc(ProjectQuestionComment::getQuestionId);
        wrapper.orderByDesc(ProjectQuestionComment::getId);
        return Collections.unmodifiableList(selectList(wrapper));
    }
}
