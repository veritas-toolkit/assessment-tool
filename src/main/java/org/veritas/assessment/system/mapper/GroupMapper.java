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

package org.veritas.assessment.system.mapper;

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
import org.veritas.assessment.common.handler.TimestampHandler;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.Group;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "group")
public interface GroupMapper extends BaseMapper<Group> {

    @Cacheable(key = "'id_'+#id", unless = "#result==null")
    default Group findById(int id) {
        return selectById(id);
    }

    @Cacheable(key = "'n_'+#name", unless = "#result==null")
    default Group findByName(String name) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getName, name);
        return selectOne(wrapper);
    }

    default int addGroup(Group group) {
        if (group.getCreatedTime() == null) {
            group.setCreatedTime(new Date());
        }
        return insert(group);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#group.id", condition = "#group != null && #group.id != null"),
                    @CacheEvict(key = "'n_'+#group.name", condition = "#group != null && #group.name != null")
            }
    )
    default int delete(Group group) {
        Objects.requireNonNull(group);
        Objects.requireNonNull(group.getId());
        Objects.requireNonNull(group.getName());
        LambdaUpdateWrapper<Group> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Group::getId, group.getId());
        group.setDeleteTime(new Date());
        wrapper.set(Group::getDeleteTime, TimestampHandler.toDbString(group.getDeleteTime()));
        wrapper.setSql("deleted = 1");
//        update(null, wrapper);
//        return deleteById(group.getId());
        return update(null, wrapper);
    }


    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#group.id", condition = "#group != null && #group.id != null"),
                    @CacheEvict(key = "'n_'+#group.name", condition = "#group != null && #group.name != null"),
                    @CacheEvict(key = "'n_'+#old.name", condition = "#old != null && #old.name != null")
            }
    )
    default int update(Group old, Group group) {
        Objects.requireNonNull(old);
        Objects.requireNonNull(group);
        if (!Objects.equals(old.getId(), group.getId())) {
            throw new IllegalArgumentException();
        }
        if (group.getLastModifiedTime() == null) {
            Date now = new Date();
            group.setLastModifiedTime(now);
        }
        Group tmp = new Group();
        tmp.setId(group.getId());
        tmp.setName(group.getName());
        tmp.setDescription(group.getDescription());
        tmp.setLastModifiedTime(group.getLastModifiedTime());
        int result = updateById(tmp);

        return result == 1 ? 1 : 0;
    }


    /**
     * Select group list by prefix of name.
     *
     * @param page     page no
     * @param pageSize page size
     * @param prefix   prefix of group's name. Can be null.
     * @return Page of group list.
     */
    default Pageable<Group> findGroupListPageable(int page, int pageSize, String prefix) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.likeRight(Group::getName, prefix);
        }
        wrapper.orderByAsc(Group::getId);
        Page<Group> groupPage = new Page<>();
        groupPage.setCurrent(page);
        groupPage.setSize(pageSize);
        Page<Group> page1 = selectPage(groupPage, wrapper);
        return Pageable.convert(page1);
    }

    /**
     * Number of groups created by {#uid}. The result can be used to limit user create too many group.
     *
     * @param uid user id
     * @return number of groups created by user.
     */
    default long numberOfGroupCreatedByUser(int uid) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getCreatorUserId, uid);
        return selectCount(wrapper);
    }

    @Deprecated
    default Pageable<Group> findGroupPageable(String prefix, String keyword, int page, int pageSize) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }

        wrapper.orderByAsc(Group::getId);
        Page<Group> groupPage = new Page<>();
        groupPage.setCurrent(page);
        groupPage.setSize(pageSize);
        Page<Group> page1 = selectPage(groupPage, wrapper);
        return Pageable.convert(page1);
    }

    default Pageable<Group> findGroupListPageable(Collection<Integer> groupIdSet,
                                                  String prefix, String keyword,
                                                  int page, int pageSize) {
        if (groupIdSet == null || groupIdSet.isEmpty()) {
            return Pageable.noRecord(page, pageSize);
        }
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }

        wrapper.in(Group::getId, groupIdSet);
        wrapper.orderByAsc(Group::getId);
        Page<Group> groupPage = new Page<>();
        groupPage.setCurrent(page);
        groupPage.setSize(pageSize);
        Page<Group> page1 = selectPage(groupPage, wrapper);
        return Pageable.convert(page1);
    }

    default List<Group> findGroupList(Collection<Integer> groupIdSet) {
        return findGroupList(groupIdSet, null, null);
    }

    default List<Group> findGroupList(Collection<Integer> groupIdSet, String prefix, String keyword) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }

        if (groupIdSet != null && !groupIdSet.isEmpty()) {
            wrapper.in(Group::getId, groupIdSet);
        }
        wrapper.orderByAsc(Group::getId);
        return selectList(wrapper);
    }

    default Pageable<Group> findGroupListPageableByCreator(Integer creatorUserId,
                                                           String prefix, String keyword,
                                                           int page, int pageSize) {
        LambdaQueryWrapper<Group> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Group::getCreatorUserId, creatorUserId);
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.apply("upper(name) like {0}", StringUtils.upperCase(prefix) + "%");
        }
        if (StringUtils.isNotEmpty(keyword)) {
            wrapper.apply("upper(name) like {0}", "%" + StringUtils.upperCase(keyword) + "%");
        }
        wrapper.orderByAsc(Group::getId);
        Page<Group> groupPage = new Page<>();
        groupPage.setCurrent(page);
        groupPage.setSize(pageSize);
        Page<Group> page1 = selectPage(groupPage, wrapper);
        return Pageable.convert(page1);
    }

}
