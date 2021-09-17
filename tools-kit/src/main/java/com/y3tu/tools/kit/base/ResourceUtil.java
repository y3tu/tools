package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.text.StrUtil;

import java.net.URL;

/**
 * 资源工具类
 *
 * @author y3tu
 */
public class ResourceUtil {

    /**
     * 获得资源的URL<br>
     * 路径用/分隔，例如:
     *
     * <pre>
     * config/a/db.config
     * spring/xml/test.xml
     * </pre>
     *
     * @param resource 资源（相对Classpath的路径）
     * @return 资源URL
     */
    public static URL getResource(String resource) {
        return getResource(resource, null);
    }

    /**
     * 获得资源相对路径对应的URL
     *
     * @param resource  资源相对路径 {@code null}和""都表示classpath根路径
     * @param baseClass 基准Class，获得的相对路径相对于此Class所在路径，如果为{@code null}则相对ClassPath
     * @return {@link URL}
     */
    public static URL getResource(String resource, Class<?> baseClass) {
        if (resource == null) {
            resource = StrUtil.EMPTY;
        }
        return baseClass != null ? baseClass.getResource(resource) : ClassLoaderUtil.getClassLoader().getResource(resource);
    }


}
