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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.veritas.assessment.common.metadata.Pageable;
import org.veritas.assessment.system.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
@Transactional
@ActiveProfiles("test")
class UserMapperTest {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void testInsert() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setFullName("full name");
        user.setUsername("username");
        user.setGroupLimited(100);
        user.setProjectLimited(200);
        user.setLoginAttempt(0);
        user.setLocked(false);
        Date now = new Date();
        user.setCreatedTime(now);
        user.setLastLoginTime(null);
        String rawPassword = RandomStringUtils.randomPrint(18);
        log.info("password: {}", rawPassword);
        user.setPassword(passwordEncoder.encode(rawPassword));
        int result = userMapper.insert(user);
        assertEquals(1, result);
        assertFalse(user.isDeleted());
        log.info("------------------------------------------------------------------------");
        log.info("user: {}", user);
        log.debug("user: {}", user);
        log.trace("user: {}", user);
        log.info("------------------------------------------------------------------------");
        log.info("after inserted, user: {}", userMapper.selectById(user.getId()));
    }

    @Test
    @DisplayName("Find user by id success.")
    void testFindById_success() {
        User user = userMapper.findById(1);
        assertNotNull(user);
        log.info("user: {}", user);
        for (int i = 0; i < 5; i++) {
            log.info("user: {}", userMapper.findById(1));
        }
        assertEquals(1, user.getId());
        assertEquals("admin", user.getUsername());
        assertFalse(user.isDeleted());
    }

    @Test
    void testFindByUsername_success() {
        User user = userMapper.findByUsername("admin");
        assertNotNull(user);
        log.info("user: {}", user);
    }

    @Test
    void testFindByEmail_success() {
        User user = userMapper.findByEmail("admin@example.com");
        assertNotNull(user);
        log.info("user: {}", user);
    }

    @Test
    void testFindByUsernameOrEmail_success() {
        User user = userMapper.findByUsernameOrEmail("admin@example.com");
        assertNotNull(user);
        log.info("user: {}", user);
        for (int i = 0; i < 5; i++) {
            log.info("find by username: {}", userMapper.findByUsernameOrEmail("admin"));
        }
    }

    @Test
    void testUpdateLastLoginTime_success() {
        User user = userMapper.selectById(1);
        user.setFullName("for_test_not_update");
        log.info("user: {}", user);
        int result = userMapper.updateLastLoginTime(user);
        assertEquals(1, result);
        User after = userMapper.selectById(1);
        log.info("After updated user: {}", after);
    }

    @Test
    void testFailLogin_success() {
        User user = userMapper.selectById(1);
        user.setLoginAttempt(6);
        user.setLocked(true);
        int result = userMapper.failLogin(user);
        assertEquals(1, result);
    }

    @Test
    void testDelete_success() {
        User user = userMapper.findById(1);
        int result = userMapper.delete(user);
        log.info("user delete time: {}", user.getDeleteTime());
        assertEquals(result, 1);
        User another = userMapper.findById(1);
        assertNull(another);
        log.info("{}", new Date().getTime());

        log.info("add again.");
        user.setDeleted(false);
        user.setDeleteTime(null);
        result = userMapper.addUser(user);
        assertEquals(1, result);
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testChangePassword_success() {
        User user = userMapper.findByUsername("test_1");
        String newPassword = "new_password";
        int result = userMapper.changePassword(user, newPassword);
        assertEquals(1, result);
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testModifyByAdmin_success() {
        User user = userMapper.findByUsername("test_1");
        user.setUsername(user.getUsername() + "xxx");
        user.setAdmin(true);
        int result = userMapper.modifyByAdmin(user);
        assertEquals(1, result);

    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testModify_success() {
        User user = userMapper.findByUsername("test_1");
        String username = "username_" + user.hashCode();
        String fullname = "fullname_" + user.hashCode();
        String email = "email_" + user.hashCode();
        User newUser = new User();
        BeanUtils.copyProperties(user, newUser, User.class);
        newUser.setUsername(username);
        newUser.setFullName(fullname);
        newUser.setEmail(email);

        int result = userMapper.modify(user, newUser);
        assertEquals(1, result);

        User afterModifyUser = userMapper.findById(user.getId());
        assertEquals(newUser, afterModifyUser);
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testLock_success() {
        int result = jdbcTemplate.update("update vat_user set locked = 0");
        assertTrue(result > 0);

        User user = userMapper.findByUsername("test_1");
        assertFalse(user.isLocked());
        result = userMapper.lock(user);
        assertEquals(1, result);
        assertTrue(user.isLocked());
        user = userMapper.findById(user.getId());
        assertTrue(user.isLocked());
        List<User> userList = userMapper.findAll();
        for (User user1 : userList) {
            if (!Objects.equals(user1.getId(), user.getId())) {
                assertFalse(user1.isLocked());
            } else {
                assertTrue(user1.isLocked());
            }
        }
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testUnlock_success() {
        int result = jdbcTemplate.update("update vat_user set locked = 1");
        assertTrue(result > 0);

        User user = userMapper.findByUsername("test_1");
        assertTrue(user.isLocked());

        result = userMapper.unlock(user);
        assertEquals(1, result);
        assertFalse(user.isLocked());
        assertEquals(0, user.getLoginAttempt());

        user = userMapper.findById(user.getId());
        assertFalse(user.isLocked());
        assertEquals(0, user.getLoginAttempt());

        List<User> userList = userMapper.findAll();
        for (User user1 : userList) {
            if (!Objects.equals(user1.getId(), user.getId())) {
                assertTrue(user1.isLocked());
            } else {
                assertFalse(user1.isLocked());
            }
        }
    }

    @Test
    @Sql(scripts = "/sql/unit_test_user.sql")
    void testFindPageable_success() {
        Pageable<User> pageable;
        pageable = userMapper.findPageable("ad", null, 1, 20);
        log.info("pageable: \n{}", pageable);
        assertEquals("admin", pageable.getRecords().get(0).getUsername());
        log.info("====================================");

        pageable = userMapper.findPageable(null, "mi", 1, 20);
        log.info("pageable: \n{}", pageable);
        assertEquals("admin", pageable.getRecords().get(0).getUsername());
        log.info("====================================");

        pageable = userMapper.findPageable(null, "test", 1, 20);
        log.info("pageable: \n{}", pageable);
        assertEquals(2, pageable.getRecords().size());
        assertEquals("test_1", pageable.getRecords().get(0).getUsername());
        log.info("====================================");

        pageable = userMapper.findPageable("t", "2", 1, 20);
        log.info("pageable: \n{}", pageable);
        assertEquals(1, pageable.getRecords().size());
        assertEquals("test_2", pageable.getRecords().get(0).getUsername());
        log.info("====================================");
    }
}