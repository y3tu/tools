package com.y3tu.tools.kit.lang;

import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;
import org.junit.Test;


public class ConsoleTest {

    @Test
    public void printProgress() {
        for (int i = 0; i < 100; i++) {
            Console.printProgress("#", 100, i / 100D);
            ThreadUtil.sleep(200);
        }
    }
}