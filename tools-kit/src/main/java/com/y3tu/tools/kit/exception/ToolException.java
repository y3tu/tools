package com.y3tu.tools.kit.exception;

/**
 * 工具异常
 *
 * @author y3tu
 */
public class ToolException extends RuntimeException {
    public ToolException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public ToolException(String message) {
        super(message);
    }

    public ToolException(String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params));
    }

    public ToolException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public ToolException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public ToolException(Throwable throwable, String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params), throwable);
    }
}
