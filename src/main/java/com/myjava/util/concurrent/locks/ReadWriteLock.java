package com.myjava.util.concurrent.locks;

/**
 * {@code ReadWriteLock} 持有一堆关联的 {@link Lock 锁}，一个用于读操作一个用于写。
 * {@linkplain #readLock 读锁}被多个读线程同时持有，然而不能有写操作。
 * {@linkplain #writeLock 写锁}却是独占的。
 *
 * 所有的{@code ReadWriteLock}实现必须保证{@code writeLock}操作的内存同步也要持有
 */
public interface ReadWriteLock {

    Lock readLock();

    Lock writeLock();
}
