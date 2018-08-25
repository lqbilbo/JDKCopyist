package com.myjava.util.concurrent;

import java.lang.ref.WeakReference;
import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个有界阻塞队列
 * 一旦创建无法更改
 * 该类支持顺序等待生产者-消费者线程的公平策略，
 * 但是默认是无保证的，除非构造器将fairness设置成true才能保障线程是FIFO的顺序进入的
 * fairness通常降低了吞吐量但是减少了可变性的同时避免了线程处于饥饿的状态
 *
 */
public class ArrayBlockingQueue<E> extends AbstractQueue<E>
    implements BlockingQueue<E>, java.io.Serializable {

    private static final long serialVersionUID = 4121687347861848208L;

    final Object[] items;

    int takeIndex;

    int putIndex;

    int count;

    /** 全局锁 */
    final ReentrantLock lock;

    private final Condition notEmpty;

    private final Condition notFull;

    /**
     * 当前活跃的迭代器的共享状态,允许队列操作更新iterator状态
     */
    transient Itrs itrs = null;

    @SuppressWarnings("unchecked")
    final E itemAt(int i) {
        return (E) items[i];
    }

    /**
     * 创建一个带有固定能力和特定进入策略的ABQ
     * @param capacity
     * @param fair
     */
    public ArrayBlockingQueue(int capacity, boolean fair) {
        if (capacity <= 0)
            throw new IllegalArgumentException();
        this.items = new Object[capacity];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    @Override
    public Iterator<E> iterator() {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public void put(E e) throws InterruptedException {

    }

    @Override
    public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }

    @Override
    public E take() throws InterruptedException {
        return null;
    }

    @Override
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        return null;
    }

    @Override
    public int remainingCapacity() {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c) {
        return 0;
    }

    @Override
    public int drainTo(Collection<? super E> c, int maxElements) {
        return 0;
    }

    @Override
    public boolean offer(E e) {
        return false;
    }

    @Override
    public E poll() {
        return null;
    }

    @Override
    public E peek() {
        return null;
    }

    /**
     * 共享迭代器与队列中间的数据，允许队列在元素被删除时更新迭代器
     *
     * 为了避免无序性，当一个队列拥有一个或多个迭代器时，它通过下面两种方式
     * 保证所有的迭代器的状态是一致的：
     * (1) 跟踪cycles数量
     * (2) 通过回调无论内部元素何时被清除都通知所有迭代器
     *
     * 在一个简单的弱引用linked list上追踪所有活动迭代器(仅当队列的锁被当前线程持有)
     * 这个list通过下面三种不同的机制进行清除工作：
     * (1) 每当新的迭代器创建之时,以O(1)复杂度进行过期元素的检测
     * (2) 每当获取索引到0的时候，检测不再使用的迭代器就多一周
     * (3) 每当队列空时，所有的迭代器都被通知并且全部的数据结构都被废弃
     */
    class Itrs {

        /**
         * 弱引用指向的linked list上的Node
         */
        private class Node extends WeakReference<Itr> {
            Node next;

            Node(Itr iterator, Node next) {
                super(iterator);
                this.next = next;
            }
        }

        int cycles = 0;

        private Node head;


        Itrs(Itr initial) {
            register(initial);
        }

        /**
         * 增加新的迭代器到跟踪iterator的linked list上
         */
        void register(Itr itr) {
            head = new Node(itr, head);
        }

        void doSomeSweeping(boolean tryHarder) {

        }

    }

    /**
     * ArrayBlockingQueue的迭代器。
     *
     * 当所有的索引都遍历之后使用"detached"模式(允许及时地从itrs中断链而不依赖GC)
     * 这样使得可以保证跟踪iterator并发更新的准确性,除非用户在hasNext()返回false
     * 之后调用Iterator.remove()的极端情况。在这种情况下,我们也能保证不会移除错误
     * 的元素。
     */
    private class Itr implements Iterator<E> {

        private int cursor;

        private E nextItem;

        private int nextIndex;

        private E lastItem;

        /**
         * 最后一项的索引值
         */
        private int lastRet;

        private int prevTakeIndex;

        private int prevCycles;

        public static final int NONE = -1;

        public static final int REMOVED = -2;

        public static final int DETACHED = -3;

        Itr() {
            lastRet = NONE;
            final ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (count == 0) {
                    cursor = NONE;
                    nextIndex = NONE;
                    prevTakeIndex = DETACHED;
                } else {
                    final int taskIndex = ArrayBlockingQueue.this.takeIndex;
                    prevTakeIndex = taskIndex;
                    nextItem = itemAt(nextIndex = taskIndex);
                    cursor = incCursor(taskIndex);
                    if (itrs == null) {
                        itrs = new Itrs(this);
                    } else {
                        itrs.register(this);
                        itrs.doSomeSweeping(false);
                    }
                    prevCycles = itrs.cycles;
                }
            } finally {
                lock.unlock();
            }
        }

        boolean isDetached() {
            assert lock.getHoldCount() == 1;
            return prevTakeIndex < 0;
        }

        private int incCursor(int index) {
            assert lock.getHoldCount() == 1;
            if (++index == items.length)
                index = 0;
            if (index == putIndex)
                index = NONE;
            return index;
        }

        private void incorporateDequeues() {
            assert lock.getHoldCount() == 1;
            assert itrs != null;
            assert !isDetached();
            assert count > 0;
            //...
        }
        @Override
        public boolean hasNext() {
            assert lock.getHoldCount() == 0;
            if (nextItem != null)
                return true;
            noNext();
            return false;
        }

        private void noNext() {
            final ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (!isDetached()) {
                    incorporateDequeues();
                    if (lastRet >= 0) {
                        lastItem = itemAt(lastRet);
                        detach();
                    }
                }
            } finally {
                lock.unlock();
            }
        }

        private void detach() {
            if (prevTakeIndex >= 0) {
                prevTakeIndex = DETACHED;
                itrs.doSomeSweeping(true);
            }
        }

        @Override
        public E next() {
            final E x = nextItem;
            if (x == null)
                throw new NoSuchElementException();
            final ReentrantLock lock = ArrayBlockingQueue.this.lock;
            lock.lock();
            try {
                if (!isDetached())
                    incorporateDequeues();
                // assert nextIndex != NONE;
                // assert lastItem == null;
                lastRet = nextIndex;
                final int cursor = this.cursor;
                if (cursor >= 0) {
                    nextItem = itemAt(nextIndex = cursor);
                    // assert nextItem != null;
                    this.cursor = incCursor(cursor);
                } else {
                    nextIndex = NONE;
                    nextItem = null;
                }
            } finally {
                lock.unlock();
            }
            return x;
        }
    }

}
