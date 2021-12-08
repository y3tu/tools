package com.y3tu.tools.lowcode.exception;

import com.y3tu.tools.kit.exception.ExceptionUtil;

/**
 * 低代码服务异常
 *
 * @author y3tu
 */
public class LowCodeException extends RuntimeException {
    public LowCodeException(Throwable e) {
        super(ExceptionUtil.getMessage(e), e);
    }

    public LowCodeException(String message) {
        super(message);
    }

    public LowCodeException(String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params));
    }

    public LowCodeException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public LowCodeException(String message, Throwable throwable, boolean enableSuppression, boolean writableStackTrace) {
        super(message, throwable, enableSuppression, writableStackTrace);
    }

    public LowCodeException(Throwable throwable, String messageTemplate, Object... params) {
        super(String.format(messageTemplate, params), throwable);
    }
}
