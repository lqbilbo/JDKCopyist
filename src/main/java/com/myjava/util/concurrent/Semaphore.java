package com.myjava.util.concurrent;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * 一个计数信号量。概念上讲,一个信号量维持了一堆许可证。直到获取许可时才能打开阻塞锁,
 * 然后拿走(资源)。每次release都会获取一个许可,潜在地增加一个阻塞等待者。
 * 然而,没有真正的许可对象被使用;信号量只是维持了一个计数器。
 *
 * 信号量经常用于限制访问资源的线程数量。
 * 一般来说,信号量经常用于控制资源访问趋于公平,为了保证访问资源的线程没有饿死的。当
 * 将信号量使用另一种同步控制方法时,非公平的有序性会超过公平的。
 */
public class Semaphore implements Serializable {
    private static final long serialVersionUID = 6374089978590857302L;
    private final Sync sync;

    abstract static class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -592265963768944249L;

        Sync(int permits) {
            setState(permits);
        }

    }

    public Semaphore(int permits) {
        sync = new NonfairSync(permits);
    }

    static final class NonfairSync extends Sync {

        private static final long serialVersionUID = 3243848912915877393L;

        NonfairSync(int permits) {
            super(permits);
        }

    }

    static final class FairSync extends Sync {

        private static final long serialVersionUID = -4513701936658845310L;

        FairSync(int permits) {
            super(permits);
        }

    }

    public Semaphore(int permits, boolean fair) {
        sync = fair ? new FairSync(permits) : new NonfairSync(permits);
    }

    /**
     * 允许中断的获取许可
     * @throws InterruptedException
     */
    public void acquire() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    /**
     * 没有中断的获取许可
     */
    public void acquireUniterruptibly() {
        sync.acquireShared(1);
    }

    /**
     * 释放许可,将其归还到信号量那里
     */
    public void release() {
        sync.releaseShared(1);
    }

    public boolean isFair() {
        return sync instanceof FairSync;
    }

    public final boolean hasQueuedThreads() {
        return sync.hasQueuedThreads();
    }

    public final int getQueueLength() {
        return sync.getQueueLength();
    }

    protected Collection<Thread> getQueuedThreads() {
        return sync.getQueuedThreads();
    }

}
