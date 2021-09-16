package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.lang.Console;
import org.junit.Test;

public class IdUtilTest {

    @Test
    public void simpleUUID() {
        Console.print(IdUtil.simpleUUID());
    }

    @Test
    public void testNextId() {
        Console.print(String.valueOf(IdUtil.nextId()));
    }
}