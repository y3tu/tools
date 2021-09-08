package com.y3tu.tools.kit.collection;

import java.util.Collection;

/**
 * 集合工具类
 *
 * @author y3tu
 */
public class CollectionUtil {

    /**
     * 判断是否为空
     *
     * @param collection 集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return (collection == null) || collection.isEmpty();
    }

    /**
     * 判断是否不为空
     *
     * @param collection 集合
     * @return 是否不为空
     */
    public static boolean isNotEmpty(Collection<?> collection) {
        return (collection != null) && !(collection.isEmpty());
    }
}
