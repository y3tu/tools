package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.lang.Snowflake;
import com.y3tu.tools.kit.text.StrUtil;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * id生成工具类
 *
 * @author y3tu
 */
public class IdUtil {

    private static Snowflake snowflake = new Snowflake(1, 1, 1);

    /**
     * 获取随机UUID
     *
     * @return 随机UUID
     */
    public static String randomUUID() {
        return fastUUID().toString();
    }

    /**
     * 简化的UUID，去掉了横线
     *
     * @return 简化的UUID，去掉了横线
     */
    public static String simpleUUID() {
        String uuid = fastUUID().toString();
        return StrUtil.replace(uuid, 0, "-", "", true);
    }

    /**
     * 返回使用ThreadLocalRandom的UUID，比默认的UUID性能更优
     *
     * @return UUID
     */
    public static UUID fastUUID() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        return new UUID(random.nextLong(), random.nextLong());
    }

    /**
     * 生成id 雪花算法
     *
     * @return id
     */
    public static long nextId() {
        return snowflake.nextId();
    }

}
