package com.app.booking.util;

import com.app.booking.common.exception.InternalServerErrorException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.lang.Assert;

import java.util.Objects;

public class AppUtils {
    private AppUtils() {
        // Prevent instantiation
    }

    public static String generateVerificationToken(String email) {
        return String.valueOf(Objects.hash(email, System.currentTimeMillis()));
    }

    public static String convertToJson(Object o) {
        Assert.notNull(o, "Object must not be null.");
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Register JavaTimeModule for handling Java 8 date/time types
            return objectMapper.writeValueAsString(o);
        } catch (JsonProcessingException var1) {
            throw new InternalServerErrorException(var1.getMessage());
        }
    }

    public static <T> T convertJsonToObject(String json, Class<T> tClass) {
        try {
            JsonMapper mapper = JsonMapper.builder()
                    .visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                    .visibility(PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE)
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .build();
            return mapper.readValue(json, tClass);
        } catch (JsonProcessingException var1) {
            throw new InternalServerErrorException(var1.getMessage());
        }

    }
}