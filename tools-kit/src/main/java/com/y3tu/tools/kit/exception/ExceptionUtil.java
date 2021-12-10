package com.y3tu.tools.kit.exception;

import java.io.PrintWriter;
import java.io.StringWriter;

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

    /**
     * 根据异常获取异常信息
     *
     * @param e 当前异常
     * @return 组装后的异常消息
     */
    public static String getFormatMessage(Exception e) {
        StackTraceElement stackTraceElement = e.getStackTrace()[0];
        String className = stackTraceElement.getClassName();
        String methodName = stackTraceElement.getMethodName();
        int lineNumber = stackTraceElement.getLineNumber();
        return "异常发生处：" + className + "." + methodName + " 第" + lineNumber + " 行\n异常简要信息：" + e.getLocalizedMessage();
    }

    /**
     * 获取堆栈信息
     *
     * @param throwable 异常
     * @return 异常堆栈信息
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        try {
            throwable.printStackTrace(pw);
            return sw.toString();
        } finally {
            pw.close();
        }
    }

    /**
     * 使用运行时异常包装编译异常<br>
     * <p>
     * 如果传入参数已经是运行时异常，则直接返回，不再额外包装
     *
     * @param throwable 异常
     * @return 运行时异常
     */
    public static RuntimeException wrapRuntime(Throwable throwable) {
        if (throwable instanceof RuntimeException) {
            return (RuntimeException) throwable;
        }
        return new RuntimeException(throwable);
    }

}
