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