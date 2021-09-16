package com.y3tu.tools.kit.base;

import com.y3tu.tools.kit.lang.Console;
import org.junit.Test;

import static org.junit.Assert.*;

public class IdUtilTest {

    @Test
    public void simpleUUID() {
        Console.print(IdUtil.simpleUUID());
    }
}