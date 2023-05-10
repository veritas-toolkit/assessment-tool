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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Repository;
import org.veritas.assessment.common.handler.TimestampHandler;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

@Repository
@CacheConfig(cacheNames = "user")
public interface UserMapper extends BaseMapper<User> {
    Logger logger = LoggerFactory.getLogger(UserMapper.class);

    @Cacheable(key = "'id_'+#id", unless = "#result==null")
    default User findById(int id) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getId, id);
        return selectOne(wrapper);
    }

    @Cacheable(unless = "#result==null")
    default User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        return selectOne(wrapper);
    }

    @Cacheable(unless = "#result==null")
    default User findByEmail(String email) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getEmail, email);
        return selectOne(wrapper);
    }

    @Cacheable(unless = "#result==null")
    default User findByUsernameOrEmail(String usernameOrEmail) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, usernameOrEmail)
                .or()
                .eq(User::getEmail, usernameOrEmail);
        return selectOne(wrapper);
    }


    default List<User> findByFuzzyPrefix(String prefix) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotEmpty(prefix)) {
            wrapper.likeRight(User::getUsername, prefix)
                    .or()
                    .likeRight(User::getFullName, prefix)
                    .or()
                    .likeRight(User::getEmail, prefix);
        }
        wrapper.orderByAsc(User::getUsername);
        return selectList(wrapper);
    }

    default List<User> findAll() {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(User::getUsername);
        return selectList(wrapper);
    }

    default Pageable<User> findPageable(String prefix, String keyword, int page, int pageSize) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotEmpty(prefix)) {
            String prefixLike = StringUtils.upperCase(prefix) + "%";

            wrapper.and(subWrapper -> {
                subWrapper.apply("upper(username) like {0}", prefixLike)
                        .or()
                        .apply("upper(full_name) like {0}", prefixLike)
                        .or()
                        .apply("upper(email) like {0}", prefixLike);
            });
        }

        if (StringUtils.isNotEmpty(keyword)) {
            String keywordLike = "%" + StringUtils.upperCase(keyword) + "%";
            wrapper.and(subWrapper -> {
                subWrapper.apply("upper(username) like {0}", keywordLike)
                        .or()
                        .apply("upper(full_name) like {0}", keywordLike)
                        .or()
                        .apply("upper(email) like {0}", keywordLike);
            });
        }


        Page<User> userPage = new Page<>();
        userPage.setCurrent(page);
        userPage.setSize(pageSize);
        Page<User> page1 = selectPage(userPage, wrapper);
        return Pageable.convert(page1);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int updateLastLoginTime(User user) {
        Date now = new Date();
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::getLoginAttempt, 0);
        wrapper.set(User::getLastLoginTime, TimestampHandler.toDbString(now));
        int result = update(null, wrapper);
        if (result > 0) {
            user.setLoginAttempt(0);
            user.setLastLoginTime(now);
        }
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int failLogin(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::getLoginAttempt, user.getLoginAttempt());
        wrapper.set(User::isLocked, user.isLocked());
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#result == 1"),
                    @CacheEvict(key = "#user.username", condition = "#result == 1"),
                    @CacheEvict(key = "#user.email", condition = "#result == 1")
            }
    )
    default int addUser(User user) {
        return insert(user);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int delete(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        user.setDeleteTime(new Date());
        wrapper.set(User::getDeleteTime, TimestampHandler.toDbString(user.getDeleteTime()));
        wrapper.setSql("deleted = 1");
        return update(null, wrapper);
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int lock(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::isLocked, true);
        int result = update(null, wrapper);
        user.setLocked(true);
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int unlock(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::isLocked, false);
        wrapper.set(User::getLoginAttempt, 0);
        int result = update(null, wrapper);
        user.setLocked(false);
        user.setLoginAttempt(0);
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int grantAdminPrivileges(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::isAdmin, true);
        int result = update(null, wrapper);
        user.setAdmin(true);
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int withdrawAdminPrivileges(User user) {
        Objects.requireNonNull(user);
        Objects.requireNonNull(user.getId());
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::isAdmin, false);
        int result = update(null, wrapper);
        if (result <= 0) {
            logger.warn("Withdraw admin privileges failed. user:[{}]", user.identification());
        }
        user.setAdmin(false);
        return result;
    }


    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#newUser.id", condition = "#newUser != null && #newUser.id != null"),
                    @CacheEvict(key = "#newUser.username", condition = "#newUser != null && #newUser.username != null"),
                    @CacheEvict(key = "#newUser.email", condition = "#newUser != null && #newUser.email != null"),
                    @CacheEvict(key = "#oldUser.username", condition = "#oldUser != null && #oldUser.username != null"),
                    @CacheEvict(key = "#oldUser.email", condition = "#oldUser != null && #oldUser.email != null")
            }
    )
    default int modify(User oldUser, User newUser) {
        Objects.requireNonNull(oldUser);
        Objects.requireNonNull(newUser);
        if (!Objects.equals(oldUser.getId(), newUser.getId())) {
            throw new IllegalArgumentException("Old and new user object do not refer to the same user.");
        }
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, oldUser.getId());
        boolean updated = false;
        if (!StringUtils.isEmpty(newUser.getUsername())) {
            updated = true;
            wrapper.set(User::getUsername, newUser.getUsername());
        }
        if (!StringUtils.isEmpty(newUser.getFullName())) {
            updated = true;
            wrapper.set(User::getFullName, newUser.getFullName());
        }
        if (!StringUtils.isEmpty(newUser.getEmail())) {
            updated = true;
            wrapper.set(User::getEmail, newUser.getEmail());
        }
        if (updated) {
            return update(null, wrapper);
        } else {
            return 0;
        }
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int changePassword(User user, String newPassword) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::getPassword, newPassword);
        wrapper.set(User::isShouldChangePassword, false);
        int result = update(null, wrapper);
        if (result == 1) {
            user.setPassword(newPassword);
            user.setShouldChangePassword(false);
        }
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int changePasswordByAdmin(User user, String newPassword) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::getPassword, newPassword);
        wrapper.set(User::isShouldChangePassword, true);
        int result = update(null, wrapper);
        if (result == 1) {
            user.setPassword(newPassword);
            user.setShouldChangePassword(true);
        }
        return result;
    }

    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int modifyByAdmin(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());

        wrapper.set(User::getUsername, user.getUsername());
        wrapper.set(User::getEmail, user.getEmail());
        wrapper.set(User::getFullName, user.getFullName());
        wrapper.set(User::isAdmin, user.isAdmin());
        wrapper.set(User::getGroupLimited, user.getGroupLimited());
        wrapper.set(User::getProjectLimited, user.getProjectLimited());
        wrapper.set(User::isLocked, user.isLocked());
        return update(null, wrapper);
    }
    @Caching(
            evict = {
                    @CacheEvict(key = "'id_'+#user.id", condition = "#user != null && #user.id != null"),
                    @CacheEvict(key = "#user.username", condition = "#user != null && #user.username != null"),
                    @CacheEvict(key = "#user.email", condition = "#user != null && #user.email != null")
            }
    )
    default int finishUserGuide(User user) {
        LambdaUpdateWrapper<User> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(User::getId, user.getId());
        wrapper.set(User::isFinishedUserGuide, true);
        int result = update(null, wrapper);
        return result;
    }
}
