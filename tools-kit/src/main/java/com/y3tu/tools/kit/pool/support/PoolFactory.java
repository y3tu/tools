package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.ObjectFactory;
import com.y3tu.tools.kit.pool.intf.Pool;
import com.y3tu.tools.kit.pool.intf.Validator;

public class PoolFactory {
    private PoolFactory() {
    }

    public static <T> Pool<T> newBoundedBlockingPool(int size, ObjectFactory<T> factory, Validator<T> validator) {
        return new BoundedBlockingPool<T>(size, validator, factory);
    }

    public static <T> Pool<T> newBoundedNonBlockingPool(int size, ObjectFactory<T> factory, Validator<T> validator) {
        return new BoundedPool<T>(size, validator, factory);
    }
}
