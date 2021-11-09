package org.veritas.assessment.common.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class DateHandlerTest {

    @Test
    void testToJavaDate() {
        String s = "2021-11-10";
        Date date = DateHandler.toJavaDate(s);
        log.info("date: {}", date);
        assertNotNull(date);
    }
}