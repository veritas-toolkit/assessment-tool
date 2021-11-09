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

package org.veritas.assessment.biz.util;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionTest {

    @Test
    void testConstructor_success() throws Exception {
        Version version = new Version("1.2.3");
        assertEquals(1, version.getMajor());
        assertEquals(2, version.getMinor());
        assertEquals(3, version.getMicro());
    }

    @Test
    void testCompare() throws Exception {
        Version version1 = new Version("1.0.0");
        Version version2 = new Version("2.0.0");
        Version version3 = new Version("2.3.0");
        assertTrue(version1.compareTo(version2) < 0);
        assertTrue(version3.compareTo(version2) > 0);
    }

    @Test
    void testNextSuggestionVersionList() throws Exception {
        Version version = new Version("1.2.3");
        List<Version> nextList = version.nextSuggestionVersionList();
        assertEquals("1.2.4", nextList.get(0).toString());
        assertEquals("1.3.0", nextList.get(1).toString());
        assertEquals("2.0.0", nextList.get(2).toString());
    }
}