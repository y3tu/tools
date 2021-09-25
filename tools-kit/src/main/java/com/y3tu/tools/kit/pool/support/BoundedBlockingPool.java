package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.BlockingPool;
import com.y3tu.tools.kit.pool.intf.PoolFactory;
import com.y3tu.tools.kit.time.DateUnit;
import com.y3tu.tools.kit.time.SystemClock;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 阻塞对象池
 *
 * @param <T> 对象类型
 * @author y3tu
 */
@Slf4j
public class BoundedBlockingPool<T> extends AbstractPool<T> implements BlockingPool<T> {
    /**
     * 记录对象数量
     */
    public final static AtomicInteger count = new AtomicInteger(0);
    /**
     * 池大小
     */
    private int coreSize;
    /**
     * 最大存放数量
     */
    private int maxSize;
    /**
     * 对象存活时间
     */
    private long keepAliveTime;
    /**
     * 时间单位
     */
    private DateUnit dateUnit;
    /**
     * 阻塞队列，对象合集
     */
    private BlockingDeque<DefaultObject> objects;
    /**
     * 对象创建工厂
     */
    private PoolFactory poolFactory;
    /**
     * 线程池
     */
    private ExecutorService executor = Executors.newCachedThreadPool();
    /**
     * 超时检查对象池
     */
    private ExecutorService checkTimeout = Executors.newSingleThreadExecutor();

    private volatile boolean shutdownCalled;

    /**
     * @param coreSize      对象池大小
     * @param maxSize       对象池最大数量
     * @param keepAliveTime 对象存活时间
     * @param dateUnit      时间单位
     * @param poolFactory   对象创建
     */
    public BoundedBlockingPool(int coreSize, int maxSize, long keepAliveTime, DateUnit dateUnit, PoolFactory poolFactory) {

        if (coreSize < 0 || maxSize <= 0 || maxSize < coreSize || keepAliveTime < 0) {
            throw new IllegalArgumentException();
        }

        if (poolFactory == null) {
            throw new NullPointerException();
        }

        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.keepAliveTime = dateUnit.toTimeUnit().toMillis(keepAliveTime);
        this.dateUnit = dateUnit;
        this.poolFactory = poolFactory;
        objects = new LinkedBlockingDeque<>();
        initObjects();
        shutdownCalled = false;
    }

    @Override
    public synchronized T get(long time, DateUnit dateUnit) throws InterruptedException {
        if (!shutdownCalled) {
            T t = null;
            try {
                if (count.get() < coreSize) {
                    log.debug("当前对象池中对象数量: " + count.get() + "  coreSize:" + coreSize);
                    for (int i = 0; i < coreSize - count.get(); i++) {
                        objects.add(new DefaultObject<>(SystemClock.now(), poolFactory.createObject()));
                        count.incrementAndGet();
                    }
                }

                if (objects.size() == 0) {
                    //对象池为空
                    if (count.get() < maxSize) {
                        objects.add(new DefaultObject<>(SystemClock.now(), poolFactory.createObject()));
                        count.incrementAndGet();
                        log.info("当前对象池中对象数量:" + count.get() + "  maxSize" + maxSize);
                    } else {
                        log.warn("当前对象池中对象数量已超过maxSize!无法创建对象");
                    }
                }
                DefaultObject base = objects.poll(time, DateUnit.toTimeUnit(dateUnit));
                return (T) base.getT();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        throw new IllegalStateException("对象池已关闭");
    }

    @Override
    public T get() {
        if (!shutdownCalled) {
            T t = null;
            try {
                if (count.get() < coreSize) {
                    //给对象池添加对象
                    synchronized (this) {
                        for (int i = 0; i < coreSize - count.get(); i++) {
                            objects.add(new DefaultObject<>(SystemClock.now(), poolFactory.createObject()));
                        }
                    }
                }
                DefaultObject base = objects.take();
                return (T) base.getT();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("对象池轮询超时", ie);
            }
        }

        throw new IllegalStateException("对象池已关闭");
    }

    @Override
    public void shutdown() {
        shutdownCalled = true;
        executor.shutdownNow();
        clearResources();
    }

    @Override
    protected void handleInvalidReturn(T t) {

    }

    @Override
    protected void returnPool(T t) {
        if (poolFactory.isValid(t)) {
            executor.submit(new ObjectReturner(objects, t));
        }
    }

    @Override
    protected boolean isValid(T t) {
        return poolFactory.isValid(t);
    }

    /**
     * 初始化对象池中的对象
     */
    private void initObjects() {

        for (int i = 0; i < coreSize; i++) {
            objects.add(new DefaultObject<>(SystemClock.now(), poolFactory.createObject()));
            count.incrementAndGet();
        }

        checkTimeout.execute(() -> {
            long cur = SystemClock.now();
            while (true) {
                if (SystemClock.now() - cur < 1000) {
                    continue;
                }

                cur = SystemClock.now();
                while (!objects.isEmpty() && SystemClock.now() - objects.getLast().getTime() > keepAliveTime) {
                    //对象超过最大存活时间，移除对象
                    objects.pollLast();
                    int c = count.decrementAndGet();
                    log.debug("对象池中可使用对象数量:" + c);
                }
                if (count.get() < coreSize) {
                    //如果当前池中的对象小于coreSize，则新增对象
                    for (int i = 0; i < coreSize - count.get(); i++) {
                        objects.add(new DefaultObject(SystemClock.now(), poolFactory.createObject()));
                    }
                }
                log.debug("当前池中对象" + objects.size());
            }
        });
    }

    private void clearResources() {
        for (DefaultObject t : objects) {
            poolFactory.invalidate((T) t.getT());
        }
    }

    private class ObjectReturner<E> implements Callable {
        private BlockingDeque blockingQueue;
        private E e;

        public ObjectReturner(BlockingDeque blockingQueue, E e) {
            this.blockingQueue = blockingQueue;
            this.e = e;
        }

        @Override
        public Void call() throws Exception {
            while (true) {
                try {
                    blockingQueue.put(new DefaultObject<>(SystemClock.now(), e));
                    break;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }
    }
}
