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
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.QuestionCommentReadLog;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Repository
@CacheConfig(cacheNames = "question_comment_read_log")
public interface QuestionCommentReadLogMapper extends BaseMapper<QuestionCommentReadLog> {


    @Cacheable(key = "'u_' + #userId + '_p_' + #projectId")
    default Map<Integer, QuestionCommentReadLog> findLog(int userId, int projectId) {
        LambdaQueryWrapper<QuestionCommentReadLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(QuestionCommentReadLog::getUserId, userId);
        wrapper.eq(QuestionCommentReadLog::getProjectId, projectId);

        List<QuestionCommentReadLog> list = selectList(wrapper);
        if (list == null || list.isEmpty()) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.toMap(QuestionCommentReadLog::getQuestionId, e -> e));
    }

    @CacheEvict(key = "'u_' + #readLog.userId + '_p_' + #readLog.projectId")
    default int addOrUpdate(QuestionCommentReadLog readLog) {
        Objects.requireNonNull(readLog);
        LambdaUpdateWrapper<QuestionCommentReadLog> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(QuestionCommentReadLog::getUserId, readLog.getUserId());
        wrapper.eq(QuestionCommentReadLog::getProjectId, readLog.getProjectId());
        wrapper.eq(QuestionCommentReadLog::getQuestionId, readLog.getQuestionId());
        wrapper.set(QuestionCommentReadLog::getLatestReadCommentId, readLog.getLatestReadCommentId());
        int result = update(null, wrapper);
        if (result == 0) {
            result = insert(readLog);
        }
        return result;
    }
}
