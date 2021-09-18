package com.y3tu.tools.kit.system;

import com.y3tu.tools.kit.text.StrUtil;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * 系统工具类
 *
 * @author y3tu
 */
public class SystemUtil {
    /**
     * Java 运行时环境版本
     */
    public final static String VERSION = "java.version";
    /**
     * Java 安装目录
     */
    public final static String HOME = "java.home";

    /**
     * 默认的临时文件路径
     */
    public final static String TMPDIR = "java.io.tmpdir";
    /**
     * 操作系统的名称
     */
    public final static String OS_NAME = "os.name";

    /**
     * 获取当前进程 PID
     *
     * @return 当前进程 ID
     */
    public static long getCurrentPID() {
        return Long.parseLong(getRuntimeMXBean().getName().split("@")[0]);
    }

    /**
     * 返回Java虚拟机运行时系统相关属性
     *
     * @return {@link RuntimeMXBean}
     */
    public static RuntimeMXBean getRuntimeMXBean() {
        return ManagementFactory.getRuntimeMXBean();
    }

    /**
     * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 defaultValue
     *
     * @param name         属性名
     * @param defaultValue 默认值
     * @return 属性值或defaultValue
     * @see System#getProperty(String)
     * @see System#getenv(String)
     */
    public static String get(String name, String defaultValue) {
        return StrUtil.nullToDefault(get(name), defaultValue);
    }

    /**
     * 取得系统属性，如果因为Java安全的限制而失败，则将错误打在Log中，然后返回 {@code null}
     *
     * @param name 属性名
     * @return 属性值或{@code null}
     * @see System#getProperty(String)
     * @see System#getenv(String)
     */
    public static String get(String name) {
        String value = null;
        try {
            value = System.getProperty(name);
        } catch (SecurityException e) {
            //ignore
        }

        if (null == value) {
            try {
                value = System.getenv(name);
            } catch (SecurityException e) {
                //ignore
            }
        }

        return value;
    }

}
