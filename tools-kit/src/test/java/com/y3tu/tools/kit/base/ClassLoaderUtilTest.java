package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.reflect.ClassLoaderUtil;
import org.junit.Test;


public class ClassLoaderUtilTest {

    @Test
    public void loadClass() {
        ClassLoaderUtil.loadClass("java.lang.Integer",null,false);
    }
}