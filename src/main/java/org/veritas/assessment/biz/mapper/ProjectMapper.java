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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.biz.entity.Project;
import org.veritas.assessment.common.handler.TimestampHandler;
import org.veritas.assessment.common.metadata.Pageable;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "project")
public interface ProjectMapper extends BaseMapper<Project> {

    @Cacheable(key = "'id_'+#id", unless = "#result==null")
    default Project findById(int id) {
        return selectById(id);
    }

    default int addProject(Project project) {
        if (project.getCreatedTime() == null) {
            project.setCreatedTime(new Date());
        }
        return insert(project);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#project.id", condition = "#result == 1"),
            }
    )
    default int delete(Project project) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(project.getId());
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, project.getId());
        project.setDeleteTime(new Date());
        wrapper.set(Project::getDeleteTime, TimestampHandler.toDbString(project.getDeleteTime()));
        wrapper.setSql("deleted = 1");
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#projectId", condition = "#result == 1"),
            }
    )
    default int archive(int projectId) {
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, projectId);
        wrapper.set(Project::isArchived, true);
        wrapper.set(Project::getLastEditedTime, TimestampHandler.toDbString(new Date()));
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#projectId", condition = "#result == 1"),
            }
    )
    default int unarchive(int projectId) {
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, projectId);
        wrapper.set(Project::isArchived, false);
        wrapper.set(Project::getLastEditedTime, TimestampHandler.toDbString(new Date()));
        return update(null, wrapper);
    }


    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#project.id", condition = "#project != null && #project.id != null"),
            }
    )
    default int updateBasicInfo(Project old, Project project) {
        Objects.requireNonNull(old);
        Objects.requireNonNull(project);
        if (!Objects.equals(old.getId(), project.getId())) {
            throw new IllegalArgumentException();
        }
        if (project.getLastEditedTime() == null) {
            Date now = new Date();
            project.setLastEditedTime(now);
        }
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, project.getId());
        if (StringUtils.isNotEmpty(project.getName())) {
            wrapper.set(Project::getName, project.getName());
        }
        if (StringUtils.isNotEmpty(project.getDescription())) {
            wrapper.set(Project::getDescription, project.getDescription());
        }
        wrapper.set(Project::isPrincipleGeneric, project.isPrincipleGeneric());
        wrapper.set(Project::isPrincipleFairness, project.isPrincipleFairness());
        wrapper.set(Project::isPrincipleEA, project.isPrincipleEA());
        wrapper.set(Project::isPrincipleTransparency, project.isPrincipleTransparency());

        wrapper.set(Project::getLastEditedTime, TimestampHandler.toDbString(project.getLastEditedTime()));
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#project.id", condition = "#project != null && #project.id != null"),
            }
    )
    default int updateQuestionnaireVid(Project project) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(project.getId());
        Objects.requireNonNull(project.getCurrentQuestionnaireVid());
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, project.getId());
        wrapper.set(Project::getCurrentQuestionnaireVid, project.getCurrentQuestionnaireVid());
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#project.id", condition = "#project != null && #project.id != null"),
            }
    )
    default int updateModelArtifactVid(Project project) {
        Objects.requireNonNull(project);
        Objects.requireNonNull(project.getId());
        Objects.requireNonNull(project.getCurrentModelArtifactVid());
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, project.getId());
        wrapper.set(Project::getCurrentModelArtifactVid, project.getCurrentModelArtifactVid());
        return update(null, wrapper);
    }

    /**
     * Number of projects created by {#uid}. The result can be used to limit user create too many project.
     *
     * @param uid user id
     * @return number of projects created by user.
     */
    default long numberOfProjectCreatedByUser(int uid) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getCreatorUserId, uid);
        return selectCount(wrapper);
    }

    default long countByGroupOwner(int groupId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getGroupOwnerId, groupId);
        return selectCount(wrapper);
    }

    default List<Project> findProjectByGroupOwner(int groupId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getGroupOwnerId, groupId);
        wrapper.orderByDesc(Project::getLastEditedTime);
        return selectList(wrapper);
    }

    default List<Project> findProjectByUserOwner(int userId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getUserOwnerId, userId);
        wrapper.orderByDesc(Project::getLastEditedTime);
        return selectList(wrapper);
    }

    /**
     * Find project by prefix and keyword
     *
     * @param prefix   project's name prefix
     * @param keyword  project's name keyword
     * @param page     page no
     * @param pageSize page size
     * @return project pageable
     */
    default Pageable<Project> findProjectPageable(String prefix,
                                                  String keyword,
                                                  int page,
                                                  int pageSize) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }
        wrapper.orderByAsc(Project::getId);
        Page<Project> projectPage = new Page<>();
        projectPage.setCurrent(page);
        projectPage.setSize(pageSize);
        Page<Project> page1 = selectPage(projectPage, wrapper);
        return Pageable.convert(page1);
    }


    /**
     * Find project which contain member {#uid}.
     *
     * @param page     page no
     * @param pageSize page size
     * @param prefix   prefix of project name.
     * @param keyword  keyword of project name.
     * @return Page of project list.
     */
    default Pageable<Project> findProjectPageable(Collection<Integer> projectIds,
                                                  Collection<Integer> groupIds,
                                                  String prefix,
                                                  String keyword,
                                                  int page,
                                                  int pageSize) {
        boolean projectEmpty = projectIds == null || projectIds.isEmpty();
        boolean groupEmpty = groupIds == null || groupIds.isEmpty();
        if (projectEmpty && groupEmpty) {
            return Pageable.noRecord(page, pageSize);
        }
        LambdaQueryWrapper<Project> wrapper = queryWrapper(projectIds, groupIds, prefix, keyword);
        Page<Project> projectPage = new Page<>();
        projectPage.setCurrent(page);
        projectPage.setSize(pageSize);
        Page<Project> page1 = selectPage(projectPage, wrapper);
        return Pageable.convert(page1);
    }

    default List<Project> findProjectList(Collection<Integer> projectIds, Collection<Integer> groupIds) {
        boolean projectEmpty = projectIds == null || projectIds.isEmpty();
        boolean groupEmpty = groupIds == null || groupIds.isEmpty();
        if (projectEmpty && groupEmpty) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Project> wrapper = queryWrapper(projectIds, groupIds, null, null);
        return selectList(wrapper);
    }

    default long countProjectOfGroup(Integer groupId) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getGroupOwnerId, groupId);
        return selectCount(wrapper);

    }

    default List<Project> findProjectList(Collection<Integer> projectIds,
                                          Collection<Integer> groupIds,
                                          String prefix,
                                          String keyword) {
        boolean projectEmpty = projectIds == null || projectIds.isEmpty();
        boolean groupEmpty = groupIds == null || groupIds.isEmpty();
        if (projectEmpty && groupEmpty) {
            return Collections.emptyList();
        }
        LambdaQueryWrapper<Project> wrapper = queryWrapper(projectIds, groupIds, prefix, keyword);
        return selectList(wrapper);
    }

    default LambdaQueryWrapper<Project> queryWrapper(Collection<Integer> projectIds,
                                                     Collection<Integer> groupIds,
                                                     String prefix,
                                                     String keyword) {
        boolean projectEmpty = projectIds == null || projectIds.isEmpty();
        boolean groupEmpty = groupIds == null || groupIds.isEmpty();
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("(upper(name) like {0} or upper(description) like {1})",
                    "%" + StringUtils.upperCase(keyword) + "%",
                    "%" + StringUtils.upperCase(keyword) + "%");
        }
        wrapper.and(ownerQueryWrapper -> {
            if (projectEmpty && groupEmpty) {
                throw new IllegalArgumentException();
            } else if (!projectEmpty && !groupEmpty) {
                ownerQueryWrapper.in(Project::getId, projectIds);
                ownerQueryWrapper.or(w -> {
                    w.in(Project::getGroupOwnerId, groupIds);
                });
            } else if (projectEmpty) {
                ownerQueryWrapper.in(Project::getGroupOwnerId, groupIds);
            } else {
                ownerQueryWrapper.in(Project::getId, projectIds);
            }
        });
        wrapper.orderByDesc(Project::getLastEditedTime);
        return wrapper;
    }

    default Pageable<Project> findProjectPageableByCreator(Integer creatorUserId,
                                                           String prefix,
                                                           String keyword,
                                                           int page,
                                                           int pageSize) {
        LambdaQueryWrapper<Project> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Project::getCreatorUserId, creatorUserId);
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }
        wrapper.orderByDesc(Project::getLastEditedTime);
        Page<Project> projectPage = new Page<>();
        projectPage.setCurrent(page);
        projectPage.setSize(pageSize);
        Page<Project> page1 = selectPage(projectPage, wrapper);
        return Pageable.convert(page1);
    }

//    default boolean updateQuestionnaireForLock(int projectId, long questionnaireVid, long questionnaireNewVid) {
    default boolean updateQuestionnaireForLock(int projectId, long questionnaireOldVid, long questionnaireNewVid) {
        LambdaUpdateWrapper<Project> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Project::getId, projectId);
        wrapper.eq(Project::getCurrentQuestionnaireVid, questionnaireOldVid);
        wrapper.set(Project::getCurrentQuestionnaireVid, questionnaireNewVid);
        int result = update(null, wrapper);
        return result > 0;
    }
}
