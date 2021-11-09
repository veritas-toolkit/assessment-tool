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

package org.veritas.assessment.system.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.common.exception.DuplicateException;
import org.veritas.assessment.system.entity.User;
import org.veritas.assessment.system.service.UserService;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
@Sql(scripts = "/sql/unit_test_user.sql")
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    public void evictAllCaches() {
        for (String name : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        }
    }

    @Test
    void testRegister_fail() {
        User user = new User();
        user.setUsername("admin");
        user.setEmail("admin@xxxx.com");
        user.setPassword("adkafjei38");
        user.setFullName("fullname");
        user.setLoginAttempt(0);
        user.setLocked(false);
        user.setGroupLimited(1);
        user.setProjectLimited(2);
        try {
            userService.register(user);
        } catch (DuplicateException exception) {
            log.warn("exception", exception);
            assertTrue(StringUtils.containsAnyIgnoreCase(exception.getMessage(), "username"));
        }
    }

    @Test
    void testModifyByAdmin_sameUsernameFail() {
        User user = userService.findUserById(2);
        user.setUsername("admin");
        try {
            userService.modifyByAdmin(user);
        } catch (DuplicateException exception) {
            assertTrue(StringUtils.containsAnyIgnoreCase(exception.getMessage(), "username"));
        }
    }

    @Test
    void testModifyByAdmin_sameEmailFail() {
        User user = userService.findUserById(3);
        user.setEmail("admin@example.com");
        boolean hasException = false;
        try {
            userService.modifyByAdmin(user);
        } catch (DuplicateException exception) {
            hasException = true;
            log.warn("exception", exception);
            assertTrue(StringUtils.containsAnyIgnoreCase(exception.getMessage(), "email"));
        }
        if (!hasException) {
            fail("Cannot run here.");
        }
    }
}