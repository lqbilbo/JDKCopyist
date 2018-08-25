package com.myjava.util.concurrent;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 一个允许多个线程都等待每个都到达栅栏的同步策略。之所以叫做cyclic(循环)是
 * 因为在等待线程被释放后可以再利用
 *
 * 在最后一个线程到达或但是所有线程释放之前,CyclicBarrier支持一个可选的Runnable
 * 命令在每个栅栏处执行。
 *
 * 当在所有的参会者到来之前更新共享状态barrier的操作是很有用的。
 */
public class CyclicBarrier {

    private static class Generation {
        boolean broken = false;
    }

    private final ReentrantLock lock = new ReentrantLock();

    private final Condition trip = lock.newCondition();

    private final int parties;

    private final Runnable barrierCommand;

    private Generation generation = new Generation();

    /** 依旧等待的参会者 */
    private int count;

    private void nextGeneration() {
        trip.signalAll();
        count = parties;
        generation = new Generation();
    }
    private void breakBarrier() {
        generation.broken = true;
        count = parties;
        trip.signalAll();
    }

    public boolean isBroken() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return generation.broken;
        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            breakBarrier();
            nextGeneration();
        } finally {
            lock.unlock();
        }
    }

    public CyclicBarrier(int parties, Runnable barrierAction) {
        if (parties <= 0) throw new IllegalArgumentException();
        this.parties = parties;
        this.count = parties;
        this.barrierCommand = barrierAction;
    }

    public CyclicBarrier(int parties) {
        this(parties, null);
    }

    public int getNumberWaiting() {
        final ReentrantLock lock = this.lock;
        lock.lock();
        try {
            return parties - count;
        } finally {
            lock.unlock();
        }
    }
}
