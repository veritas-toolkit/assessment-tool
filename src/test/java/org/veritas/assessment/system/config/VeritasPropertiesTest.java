package org.veritas.assessment.system.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@Slf4j
@Transactional
@SpringBootTest
@ActiveProfiles("test")
class VeritasPropertiesTest {

    @Autowired
    VeritasProperties veritasProperties;

    @Test
    void name() {
        assertNotNull(veritasProperties);
    }
}