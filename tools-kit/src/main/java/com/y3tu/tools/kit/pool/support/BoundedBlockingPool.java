package com.y3tu.tools.kit.pool.support;

import com.y3tu.tools.kit.pool.intf.BlockingPool;
import com.y3tu.tools.kit.pool.intf.ObjectFactory;
import com.y3tu.tools.kit.pool.intf.Validator;
import com.y3tu.tools.kit.time.DateUnit;

import java.util.concurrent.*;

public class BoundedBlockingPool<T> extends AbstractPool<T> implements BlockingPool<T> {

    private int size;
    private BlockingQueue<T> objects;
    private Validator<T> validator;
    private ObjectFactory objectFactory;
    private ExecutorService executor = Executors.newCachedThreadPool();
    private volatile boolean shutdownCalled;

    public BoundedBlockingPool(int size, Validator<T> validator,
                               ObjectFactory objectFactory) {
        super();
        this.objectFactory = objectFactory;
        this.size = size;
        this.validator = validator;
        objects = new LinkedBlockingQueue<T>(size);
        initializeObjects();
        shutdownCalled = false;
    }

    @Override
    public T get(long time, DateUnit dateUnit) throws InterruptedException {
        if (!shutdownCalled) {
            T t = null;
            try {
                t = (T) objects.poll(time, DateUnit.toTimeUnit(dateUnit));
                return t;
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }
        throw new IllegalStateException("Object pool is already shutdown");
    }

    @Override
    public T get() {
        if (!shutdownCalled) {
            T t = null;
            try {
                t = (T) objects.take();
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
            return t;
        }

        throw new IllegalStateException("Object pool is already shutdown");
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
        if (validator.isValid(t)) {
            executor.submit(new ObjectReturner(objects, t));
        }
    }

    @Override
    protected boolean isValid(T t) {
        return validator.isValid(t);
    }

    private void initializeObjects() {
        for (int i = 0; i < size; i++) {
            objects.add((T) objectFactory.createNew());
        }
    }

    private void clearResources() {
        for (T t : objects) {
            validator.invalidate(t);
        }
    }

    private class ObjectReturner<E> implements Callable<E> {
        private BlockingQueue queue;
        private E e;

        public ObjectReturner(BlockingQueue queue, E e) {
            this.queue = queue;
            this.e = e;
        }

        @Override
        public E call() {
            while (true) {
                try {
                    queue.put(e);
                    break;
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
            return null;
        }

    }
}
