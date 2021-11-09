package org.veritas.assessment.common.metadata;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PageableTest {


    @Test
    void testNoRecord_success() {
        Pageable<RecordObject> pageable = Pageable.noRecord(1, 20);
        assertNotNull(pageable);
        assertNotNull(pageable.getRecords());
        assertTrue(pageable.getRecords().isEmpty());
        assertEquals(1, pageable.getPage());
        assertEquals(20, pageable.getPageSize());
        assertEquals(0, pageable.getTotal());

    }

    @Data
    public static class RecordObject {
        int foo;
        String bar;
    }
}