package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.lang.Console;
import com.y3tu.tools.kit.reflect.ClassUtil;
import org.junit.Test;


public class ClassUtilTest {

    @Test
    public void getClassName() {
        Console console = new Console();
        Console.log(ClassUtil.getClassName(console, false));
    }
}