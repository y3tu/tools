package com.y3tu.tools.kit.math;

import com.y3tu.tools.kit.lang.Console;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;


public class NumberUtilTest {

    @Test
    public void add() {
        float a = 1.2f;
        double b = 2.32;
        float c = NumberUtil.add(a, b).floatValue();
        Console.log(c + "");
    }

    @Test
    public void div() {
        String a = "5";
        String b = "3";
        BigDecimal bigDecimal = NumberUtil.div(a, b, 3, RoundingMode.HALF_UP);
        Console.log(bigDecimal.floatValue()+"");
    }
}