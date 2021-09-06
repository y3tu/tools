package com.y3tu.tools.kit.collection;

/**
 * 数组工具类
 *
 * @author y3tu
 */
public class ArrayUtil {

    /**
     * 数组是否为空
     *
     * @param array 数组
     * @param <T>   数组元素类型
     * @return 是否为空
     */
    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    /**
     * 数组是否不为空
     *
     * @param array 数组
     * @param <T>   数组元素类型
     * @return 是否不为空
     */
    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }


}
