package com.y3tu.tools.kit.system;

import com.y3tu.tools.kit.lang.Console;
import org.junit.Test;


public class SystemUtilTest {

    @Test
    public void get() {
        Console.log(SystemUtil.get(SystemUtil.TMPDIR),"222",333);
    }
}