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

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class Version implements Serializable, Comparable<Version> {
    public static final List<Version> SUGGESTION_FIRST_VERSION_LIST;

    static {
        List<Version> result = new ArrayList<>(3);
        result.add(new Version(0, 0, 1));
        result.add(new Version(0, 1, 0));
        result.add(new Version(1, 0, 0));
        SUGGESTION_FIRST_VERSION_LIST = Collections.unmodifiableList(result);
    }

    private final int major;
    private final int minor;
    private final int micro;

    public Version(int major, int minor, int micro) {
        this.major = major;
        this.minor = minor;
        this.micro = micro;
    }

    public Version(String versionString) throws Exception {
        String[] array = StringUtils.split(versionString, ".");
        if (array == null || array.length < 3) {
            throw new IllegalArgumentException();
        }
        Integer[] vArray = new Integer[3];
        try {
            for (int i = 0; i < 3; i++) {
                vArray[i] = Integer.parseInt(array[i]);
            }
        } catch (NumberFormatException exception) {
            throw new Exception(String.format("[%s] is not a legal version.", versionString), exception);
        }
        this.major = vArray[0];
        this.minor = vArray[1];
        this.micro = vArray[2];
    }

    public static boolean isValidVersion(String versionString) {
        try {
            Version version = new Version(versionString);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }


    @Override
    public int compareTo(Version right) {
        int majorResult = Integer.compare(this.major, right.major);
        if (majorResult != 0) {
            return majorResult;
        }
        int minorResult = Integer.compare(this.minor, right.minor);
        if (minorResult != 0) {
            return minorResult;
        }
        return Integer.compare(this.micro, right.micro);
    }

    @Override
    public String toString() {
        return String.format("%d.%d.%d", major, minor, micro);
    }

    public List<Version> nextSuggestionVersionList() {
        List<Version> result = new ArrayList<>(3);
        result.add(new Version(major, minor, micro + 1));
        result.add(new Version(major, minor + 1, 0));
        result.add(new Version(major + 1, 0, 0));
        return Collections.unmodifiableList(result);
    }
}
