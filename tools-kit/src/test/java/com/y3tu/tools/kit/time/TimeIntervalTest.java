package com.y3tu.tools.kit.time;

import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;
import com.y3tu.tools.kit.lang.Console;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class TimeIntervalTest {

    @Test
    public void interval() {
        TimeInterval timeInterval = new TimeInterval();
        timeInterval.start();
        ThreadUtil.sleep(2000);
        long a = timeInterval.interval("");
        Console.print(String.valueOf(a));
    }
}