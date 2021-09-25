package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.PoolFactory;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

/**
 * 一个非阻塞的对象池的实现，即使对象不可用，它也不会让客户端阻塞，而是直接返回null
 *
 * @param <T> 对象类型
 * @author y3tu
 */
public class BoundedPool<T> extends AbstractPool<T> {
    /**
     * 对象池大小
     */
    private int size;
    /**
     * 队列 存放对象
     */
    private Queue<T> objects;
    /**
     * 对象创建
     */
    private PoolFactory<T> poolFactory;
    /**
     * 信号量
     */
    private Semaphore permits;
    /**
     * 调用后是否关闭对象
     */
    private volatile boolean shutdownCalled;

    public BoundedPool(int size, PoolFactory<T> poolFactory) {
        super();
        this.poolFactory = poolFactory;
        this.size = size;
        objects = new LinkedList<T>();
        initializeObjects();
        shutdownCalled = false;
    }

    @Override
    public T get() {
        T t = null;
        if (!shutdownCalled) {
            if (permits.tryAcquire()) {
                //轮询获取对象
                t = objects.poll();
            }
        } else {
            throw new IllegalStateException("Object pool already shutdown");
        }
        return t;
    }

    @Override
    public void shutdown() {
        shutdownCalled = true;
        clearResources();
    }

    @Override
    protected void handleInvalidReturn(T t) {

    }

    @Override
    protected void returnPool(T t) {
        boolean added = objects.add(t);
        if (added) {
            permits.release();
        }
    }

    @Override
    protected boolean isValid(T t) {
        return poolFactory.isValid(t);
    }

    /**
     * 初始化创建对象，并放入队列中
     */
    private void initializeObjects() {
        //创建size个对象
        for (int i = 0; i < size; i++) {
            objects.add(poolFactory.createObject());
        }
    }

    /**
     * 清除对象
     */
    private void clearResources() {
        for (T t : objects) {
            poolFactory.invalidate(t);
        }
    }
}
