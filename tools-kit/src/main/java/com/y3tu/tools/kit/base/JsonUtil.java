package com.y3tu.tools.kit.base;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.y3tu.tools.kit.exception.ToolException;

import java.util.List;

/**
 * Json字符串工具
 *
 * @author y3tu
 */
public class JsonUtil {

    /**
     * 定义jackson对象
     */
    private static ObjectMapper MAPPER = new ObjectMapper();

    /**
     * Object转成JSON数据
     *
     * @param obj 需要转换的对象
     * @return JSON字符串
     */
    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ToolException(e);
        }
    }

    /**
     * JSON字符串转成Object
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   返回类型
     * @return 转换后的实体
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            T t = MAPPER.readValue(json, clazz);
            return t;
        } catch (JsonProcessingException e) {
            throw new ToolException(e);
        }
    }

    /**
     * JSON字符串转成List
     *
     * @param json  JSON字符串
     * @param clazz 目标类型
     * @param <T>   返回类型
     * @return 转换后的实体
     */
    public static <T> List<T> parseList(String json, Class<T> clazz) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return MAPPER.readValue(json, javaType);
        } catch (JsonProcessingException e) {
            throw new ToolException(e);
        }
    }
}
