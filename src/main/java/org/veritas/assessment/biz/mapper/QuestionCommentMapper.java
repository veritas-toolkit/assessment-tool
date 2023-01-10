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
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.QuestionComment;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "project_question_comment")
public interface QuestionCommentMapper extends BaseMapper<QuestionComment> {

    @Caching(
            evict = {
                    @CacheEvict(key = "'q_' + #comment.questionId"),
                    @CacheEvict(key = "'p_' + #comment.projectId")
            }
    )
    default int add(QuestionComment comment) {
        Objects.requireNonNull(comment);
        if (comment.getCreatedTime() == null) {
            comment.setCreatedTime(new Date());
        }
        return insert(comment);
    }

    @Cacheable(key = "'q_' + #questionId", unless = "#result==null")
    default List<QuestionComment> findByQuestionId(Integer questionId) {
        Objects.requireNonNull(questionId);
        LambdaQueryWrapper<QuestionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionComment::getQuestionId, questionId);
        wrapper.orderByDesc(QuestionComment::getId);
        return Collections.unmodifiableList(selectList(wrapper));
    }

    @Cacheable(key = "'p_' + #projectId", unless = "#result==null")
    default List<QuestionComment> findByProjectId(Integer projectId) {
        Objects.requireNonNull(projectId);
        LambdaQueryWrapper<QuestionComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionComment::getProjectId, projectId);
        wrapper.orderByAsc(QuestionComment::getQuestionId);
        wrapper.orderByDesc(QuestionComment::getId);
        return Collections.unmodifiableList(selectList(wrapper));
    }
}
