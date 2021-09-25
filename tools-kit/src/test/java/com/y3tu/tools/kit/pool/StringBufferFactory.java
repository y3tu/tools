package com.y3tu.tools.kit.pool;


import com.y3tu.tools.kit.pool.intf.PoolFactory;

public class StringBufferFactory implements PoolFactory<StringBuffer> {

    @Override
    public StringBuffer createObject() {
        return new StringBuffer();
    }

    @Override
    public boolean isValid(StringBuffer stringBuffer) {
        if (stringBuffer == null) return false;
        return true;
    }

    @Override
    public void invalidate(StringBuffer stringBuffer) {
        stringBuffer = null;
    }
}
