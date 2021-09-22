package com.y3tu.tools.web.cache.core.redis;

import com.y3tu.tools.kit.base.JsonUtil;
import com.y3tu.tools.kit.lang.Assert;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 自定义序列化器
 *
 * @author y3tu
 */
public class StringRedisSerializer implements RedisSerializer<Object> {

    private final Charset charset;

    private final String target = "\"";

    private final String replacement = "";

    public StringRedisSerializer() {
        this(StandardCharsets.UTF_8);
    }

    public StringRedisSerializer(Charset charset) {
        Assert.notNull(charset, "Charset must not be null!");
        this.charset = charset;
    }

    @Override
    public String deserialize(byte[] bytes) {
        return (bytes == null ? null : new String(bytes, charset));
    }

    @Override
    public byte[] serialize(Object object) {
        String string = JsonUtil.toJson(object);
        if (string == null) {
            return null;
        }
        string = string.replace(target, replacement);
        return string.getBytes(charset);
    }
}
