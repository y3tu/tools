package com.y3tu.tools.web.cache.core;


import com.y3tu.tools.kit.concurrent.thread.RejectPolicy;
import com.y3tu.tools.kit.concurrent.thread.ThreadUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

/**
 * 缓存默认创建一个常住线程池
 *
 * @author y3tu
 */
public class CacheThreadTask {
    private static ExecutorService executor = null;

    static {
        ThreadFactory threadFactory = ThreadUtil.newNamedThreadFactory("tools-cache", true);
        executor = ThreadUtil.newExecutor(8, 64, 1000, 120, threadFactory, RejectPolicy.DISCARD);
    }

    public static void run(Runnable runnable) {
        executor.execute(runnable);
    }
}
