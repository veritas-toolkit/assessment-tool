package org.veritas.assessment;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

public class TestUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String toJson(Object object)  {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        objectMapper.setDateFormat(df);
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
        } catch (JsonProcessingException exception) {
            throw new RuntimeException("to json failed.", exception);
        }

    }
}
