package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.lang.Console;
import org.junit.Test;


public class ClassUtilTest {

    @Test
    public void getClassName() {
        Console console = new Console();
        Console.print(ClassUtil.getClassName(console, false));
    }
}