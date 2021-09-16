package com.y3tu.tools.kit.base;

/**
 * 类工具
 *
 * @author y3tu
 */
public class ClassUtil {

    /**
     * 获取类名
     * 类名并不包含“.class”这个扩展名
     * isSimple为false: "com.xiaoleilu.hutool.util.ClassUtil"
     * isSimple为true: "ClassUtil"
     *
     * @param clazz    类
     * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
     * @return 类名
     */
    public static String getClassName(Class<?> clazz, boolean isSimple) {
        if (clazz == null) {
            return null;
        }
        return isSimple ? clazz.getSimpleName() : clazz.getName();
    }

    /**
     * 获取类名
     *
     * @param obj      获取类名对象
     * @param isSimple 是否简单类名，如果为true，返回不带包名的类名
     * @return 类名
     */
    public static String getClassName(Object obj, boolean isSimple) {
        if (obj == null) {
            return null;
        }
        final Class<?> clazz = obj.getClass();
        return getClassName(clazz, isSimple);
    }
}
