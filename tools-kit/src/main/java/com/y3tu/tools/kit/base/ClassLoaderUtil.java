package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.exception.ToolException;

import javax.tools.Tool;
import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 * ClassLoader工具类
 *
 * @author y3tu
 */
public class ClassLoaderUtil {

    /**
     * 获取当前线程的ClassLoader
     *
     * @return 当前线程的ClassLoader
     */
    public static ClassLoader getContextClassLoader() {
        if (System.getSecurityManager() == null) {
            return Thread.currentThread().getContextClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) () -> Thread.currentThread().getContextClassLoader());
        }
    }

    /**
     * 获取系统{@link ClassLoader}
     *
     * @return 系统{@link ClassLoader}
     * @see ClassLoader#getSystemClassLoader()
     */
    public static ClassLoader getSystemClassLoader() {
        if (System.getSecurityManager() == null) {
            return ClassLoader.getSystemClassLoader();
        } else {
            // 绕开权限检查
            return AccessController.doPrivileged(
                    (PrivilegedAction<ClassLoader>) ClassLoader::getSystemClassLoader);
        }
    }

    /**
     * 获取{@link ClassLoader}<br>
     * 获取顺序如下：<br>
     *
     * <pre>
     * 1、获取当前线程的ContextClassLoader
     * 2、获取当前类对应的ClassLoader
     * 3、获取系统ClassLoader（{@link ClassLoader#getSystemClassLoader()}）
     * </pre>
     *
     * @return 类加载器
     */
    public static ClassLoader getClassLoader() {
        ClassLoader classLoader = getContextClassLoader();
        if (classLoader == null) {
            classLoader = ClassLoaderUtil.class.getClassLoader();
            if (null == classLoader) {
                classLoader = getSystemClassLoader();
            }
        }
        return classLoader;
    }

    /**
     * 加载类，通过传入类的字符串，返回其对应的类名
     *
     * @param name          类名
     * @param classLoader   加载器
     * @param isInitialized 是否初始化类（调用static模块内容和初始化static属性）
     * @return 类名对应的类
     */
    public static Class<?> loadClass(String name, ClassLoader classLoader, boolean isInitialized) {
        if (classLoader == null) {
            classLoader = getClassLoader();
        }
        Class<?> clazz = null;
        try {
            clazz = Class.forName(name, isInitialized, classLoader);
        } catch (Exception e) {
            throw new ToolException("类加载异常！" + e.getMessage(), e);
        }
        return clazz;
    }
}
