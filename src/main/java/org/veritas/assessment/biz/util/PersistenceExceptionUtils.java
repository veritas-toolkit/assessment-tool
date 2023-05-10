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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

@Slf4j
public class PersistenceExceptionUtils {
    private static final Set<SQLiteErrorCode> DUPLICATED_ERROR_CODE_SET;

    static {
        Set<SQLiteErrorCode> set = EnumSet.noneOf(SQLiteErrorCode.class);
        set.add(SQLiteErrorCode.SQLITE_CONSTRAINT_PRIMARYKEY);
        set.add(SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE);
        DUPLICATED_ERROR_CODE_SET = Collections.unmodifiableSet(set);
    }


    public static boolean isUniqueConstraintException(Exception exception) {
        SQLiteException sqLiteException = find(exception);
        if (sqLiteException != null) {

            log.warn("sqlite exception", sqLiteException);
            SQLiteErrorCode sqLiteErrorCode = sqLiteException.getResultCode();
            return DUPLICATED_ERROR_CODE_SET.contains(sqLiteErrorCode);
        }

        StringBuilder builder = new StringBuilder(exception.getMessage());
        if (exception.getCause() != null) {
            builder.append(" ").append(exception.getCause().getMessage());
        }
        return StringUtils.containsAnyIgnoreCase(builder, "UNIQUE constraint");
    }

    private static SQLiteException find(Throwable exception) {
        Throwable throwable = exception;
        while (throwable != null) {
            if (throwable instanceof SQLiteException) {
                return (SQLiteException) throwable;
            }
            if (throwable.getCause() != throwable) {
                throwable = throwable.getCause();
            }
        }
        return null;
    }
}
