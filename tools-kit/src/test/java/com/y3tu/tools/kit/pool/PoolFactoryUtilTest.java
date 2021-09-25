package com.y3tu.tools.kit.pool;

import com.y3tu.tools.kit.pool.support.BoundedBlockingPool;
import com.y3tu.tools.kit.pool.support.PoolFactoryUtil;
import com.y3tu.tools.kit.time.DateUnit;
import org.junit.Test;

public class PoolFactoryUtilTest {

    @Test
    public void newBoundedBlockingPool() {

        BoundedBlockingPool<StringBuffer> pool = PoolFactoryUtil.newBoundedBlockingPool(5, 10, 1000000, DateUnit.MILLISECOND, new StringBufferFactory());
        try {
            StringBuffer stringBuffer1 = pool.get(1000, DateUnit.MILLISECOND);
            StringBuffer stringBuffer2 = pool.get(1000, DateUnit.MILLISECOND);
            StringBuffer stringBuffer3 = pool.get(1000, DateUnit.MILLISECOND);
            StringBuffer stringBuffer4 = pool.get(1000, DateUnit.MILLISECOND);
            StringBuffer stringBuffer5 = pool.get(1000, DateUnit.MILLISECOND);
            StringBuffer stringBuffer6 = pool.get(1000, DateUnit.MILLISECOND);
            stringBuffer6.append("hello");
            pool.release(stringBuffer4);
            pool.release(stringBuffer5);
            pool.release(stringBuffer6);
            long cur = System.currentTimeMillis();
            while (true) {
//                    long cur = System.currentTimeMillis();
                if (System.currentTimeMillis() - cur > 10) {
                    break;
                }
            }
            StringBuffer stringBuffer7 = pool.get(1000, DateUnit.MILLISECOND);
            System.out.println("stringBuffer7" + stringBuffer7.toString());
            StringBuffer stringBuffer8 = pool.get(1000, DateUnit.MILLISECOND);
            System.out.println("stringBuffer8" + stringBuffer8.toString());
            StringBuffer stringBuffer9 = pool.get(1000, DateUnit.MILLISECOND);
            System.out.println("stringBuffer9" + stringBuffer9.toString());
            StringBuffer stringBuffer10 = pool.get(1000, DateUnit.MILLISECOND);
            System.out.println("stringBuffer10" + stringBuffer10.toString());
            StringBuffer stringBuffer11 = pool.get(1000, DateUnit.MILLISECOND);
            System.out.println("stringBuffer11" + stringBuffer11.toString());

            System.out.println("----------------finish!");
            cur = System.currentTimeMillis();
            while (true) {
//                    long cur = System.currentTimeMillis();
                if (System.currentTimeMillis() - cur > 2000) {
                    break;
                }
            }
            System.out.println(BoundedBlockingPool.count);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}