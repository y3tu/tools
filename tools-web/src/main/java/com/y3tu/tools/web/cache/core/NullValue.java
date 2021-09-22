package com.y3tu.tools.web.cache.core;

import java.io.Serializable;

/**
 * 空值的包装实体
 *
 * @author y3tu
 */
public final class NullValue implements Serializable {

    public static final Object INSTANCE = new NullValue();

    private static final long serialVersionUID = 1L;

    private Object readResolve() {
        return INSTANCE;
    }
}
