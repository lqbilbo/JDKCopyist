package com.myjava.util.concurrent.atomic;

import jdk.internal.misc.Unsafe;

import java.io.Serializable;
import java.lang.invoke.VarHandle;
import java.util.function.IntBinaryOperator;
import java.util.function.IntUnaryOperator;

/**
 * {@code int}值的更新是原子的。{@link java.lang.invoke.VarHandle}指定描述了原子访问的属性。
 * {@code AtomicInteger}被用于在应用中原子地增加计数器，而且不能用于代替{@link Integer}。然而
 * 这个类继承了{@code Number}来允许通过工具和实用类指定访问来处理基于数值的类
 */
public class AtomicInteger extends Number implements Serializable {
    private static final long serialVersionUID = 6803068572428032944L;

    private static final jdk.internal.misc.Unsafe U = Unsafe.getUnsafe();
    private static final long VALUE = U.objectFieldOffset(AtomicInteger.class, "value");

    private volatile int value;

    public AtomicInteger(int initialValue) {
        this.value = initialValue;
    }

    public AtomicInteger() {
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return 0;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }

    /**
     * 返回当前值,
     * 通过{@link VarHandle#getVolatile}作用于内存。
     *
     * @return the current value
     */
    public final int get() {
        return value;
    }

    /**
     * 将值设置成{@code newValue},
     * 通过{@link VarHandle#setVolatile}作用于内存。
     *
     * @param newValue the new value
     */
    public final void set(int newValue) {
        value = newValue;
    }

    public final void lazySet(int newValue) {
        U.putIntRelease(this, value, newValue);
    }

    public final int getAndSet(int newValue) {
        return U.getAndSetInt(this, VALUE, newValue);
    }

    public final boolean compareAndSet(int expectedValue, int newValue) {
        return U.compareAndSetInt(this, VALUE, expectedValue, newValue);
    }

    public final boolean weakCompareAndSetPlain(int expectedValue, int newValue) {
        return U.weakCompareAndSetIntPlain(this, VALUE, expectedValue, newValue);
    }

    public final int getAndUpdate(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext) {
                next = updateFunction.applyAsInt(prev);
            }
            if (weakCompareAndSetVolatile(prev, next)) {
                return prev;
            }
            haveNext = (prev == (prev = get()));
        }
    }

    public final int updateAndGet(IntUnaryOperator updateFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext) {
                next = updateFunction.applyAsInt(prev);
            }
            if (weakCompareAndSetVolatile(prev, next)) {
                return next;
            }
            haveNext = (prev == (prev = get()));
        }
    }

    public final int getAndAccumulate(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext) {
                next = accumulatorFunction.applyAsInt(prev, x);
            }
            if (weakCompareAndSetVolatile(prev, next)) {
                return prev;
            }
            haveNext = (prev == (prev = get()));
        }
    }

    public final int accumulateAndGet(int x, IntBinaryOperator accumulatorFunction) {
        int prev = get(), next = 0;
        for (boolean haveNext = false;;) {
            if (!haveNext) {
                next = accumulatorFunction.applyAsInt(prev, x);
            }
            if (weakCompareAndSetVolatile(prev, next)) {
                return next;
            }
            haveNext = (prev == (prev = get()));
        }
    }

    public final int compareAndExchange(int expectedValue, int newValue) {
        return U.compareAndExchangeInt(this, VALUE, expectedValue, newValue);
    }

    public final int compareAndExchangeAcquire(int expectedValue, int newValue) {
        return U.compareAndExchangeIntAcquire(this, VALUE, expectedValue, newValue);
    }

    public final boolean weakCompareAndSetVolatile(int expectedValue, int newValue) {
        return U.weakCompareAndSetInt(this, VALUE, expectedValue, newValue);
    }
}

