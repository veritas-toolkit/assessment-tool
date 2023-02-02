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

package org.veritas.assessment.common.handler;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimestampHandler extends BaseTypeHandler<Date> {
//    private static final String JAVA_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String JAVA_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    private static final TimeZone UTC_TIMEZONE = TimeZone.getTimeZone("UTC");

    public TimestampHandler() {
    }

    public static String toDbString(Date date) {
        if (date == null) {
            return null;
        } else {
            return DateFormatUtils.formatUTC(date, JAVA_DATE_FORMAT);
        }
    }

    public static Date toJavaDate(String dateString) {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        if (StringUtils.equalsIgnoreCase("null", dateString)) {
            return null;
        }
        try {
            String format = StringUtils.substring(JAVA_DATE_FORMAT, 0, dateString.length());
//            DateFormat dateFormat = new SimpleDateFormat(JAVA_DATE_FORMAT);
            DateFormat dateFormat = new SimpleDateFormat(format);
            dateFormat.setTimeZone(UTC_TIMEZONE);
            return dateFormat.parse(dateString);
        } catch (ParseException exception) {
            throw new RuntimeException(String.format("Error data[%s].", dateString), exception);
        }
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Date parameter, JdbcType jdbcType)
            throws SQLException {
        String dateString = toDbString(parameter);
        ps.setString(i, dateString);
    }

    @Override
    public Date getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String dateString = rs.getString(columnName);
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        return toJavaDate(dateString);
    }

    @Override
    public Date getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String dateString = rs.getString(columnIndex);
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        return toJavaDate(dateString);
    }

    @Override
    public Date getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String dateString = cs.getString(columnIndex);
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        return toJavaDate(dateString);
    }
}
