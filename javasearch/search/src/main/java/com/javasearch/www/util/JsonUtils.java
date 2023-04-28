package com.javasearch.www.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JsonUtils {

    public static final ObjectMapper mapper = new ObjectMapper();


    @Nullable
    public static String toString(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            Logger.error("json序列化出错");
            return null;
        }
    }

    @Nullable
    public static <T> T toObj(String json, Class<T> tClass) {
        try {
            return mapper.readValue(json, tClass);
        } catch (JsonProcessingException e) {
            Logger.error("json反序列化出错");
            return null;
        }
    }

    @Nullable
    public static <E> List<E> toList(String json, Class<E> eClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructCollectionType(List.class, eClass));
        } catch (IOException e) {
            Logger.error("json序列化出错");
            return null;
        }
    }

    @Nullable
    public static <K, V> Map<K, V> toMap(String json, Class<K> kClass, Class<V> vClass) {
        try {
            return mapper.readValue(json, mapper.getTypeFactory().constructMapType(Map.class, kClass, vClass));
        } catch (IOException e) {
            Logger.error("json序列化出错");
            return null;
        }
    }

    @Nullable
    public static <T> T nativeRead(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            Logger.error("json序列化出错");
            return null;
        }
    }
}
