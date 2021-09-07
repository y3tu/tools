package com.y3tu.tools.kit.exception;

/**
 * 异常工具类
 *
 * @author y3tu
 */
public class ExceptionUtil {
    /**
     * 获得完整消息，包括异常名，消息格式为：{SimpleClassName}: {ThrowableMessage}
     *
     * @param e 异常
     * @return 完整消息
     */
    public static String getMessage(Throwable e) {
        if (null == e) {
            return "";
        }
        return String.format("%s:%s", e.getClass().getSimpleName(), e.getMessage());
    }

    /**
     * 获得消息，调用异常类的getMessage方法
     *
     * @param e 异常
     * @return 消息
     */
    public static String getSimpleMessage(Throwable e) {
        return (null == e) ? "" : e.getMessage();
    }


}
