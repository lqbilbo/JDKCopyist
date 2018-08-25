package com.myjava.util.concurrent;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * CountDownLatch是通过给定的count进行初始化。
 * await方法一直阻塞知道当前的count降至0,在所有等待线程都释放并且await调用返回的时候。
 * 但有一个关键的现象——即count不能重置。如果你需要一个可以重置的计数器,可以使用CyclicBarrier
 *
 * CountDownLatch由一个可以当做简单的可开闭的门闩或门的东西的计数器来初始化。所有线程调用await
 * 方法在门前等待指导一个线程调用了countDown之后计数器变为了0.
 *
 * CountDownLatch的一个重要属性是它在继续前进之前不需要阻塞在countDown方法上等待计数器到达0,它
 * 仅仅通过await阻止了所有的线程继续前进指导可以的时候。
 */
public class CountDownLatch {

    private static final class Sync extends AbstractQueuedSynchronizer {

        private static final long serialVersionUID = -7066182150169593104L;

        Sync(int count) {
            setState(count);
        }

        int getCount() {
            return getState();
        }

    }

    private final Sync sync;

    public CountDownLatch(int count) {
        if (count < 0)
            throw new IllegalArgumentException("count < 0");
        this.sync = new Sync(count);
    }

    public void await() throws InterruptedException {
        sync.acquireSharedInterruptibly(1);
    }

    public void countDown() {
        sync.releaseShared(1);
    }

    public long getCount() {
        return sync.getCount();
    }

}
