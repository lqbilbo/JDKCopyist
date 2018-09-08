package com.myjava.util.concurrent.atomic;

import sun.misc.Unsafe;

import java.io.Serializable;
import java.lang.invoke.VarHandle;

/**
 * {@code int}值的更新是原子的。{@link java.lang.invoke.VarHandle}指定描述了原子访问的属性。
 * {@code AtomicInteger}被用于在应用中原子地增加计数器，而且不能用于代替{@link Integer}。然而
 * 这个类继承了{@code Number}来允许通过工具和实用类指定访问来处理基于数值的类
 */
public class AtomicInteger extends Number implements Serializable {
    private static final long serialVersionUID = 6803068572428032944L;

    private static final Unsafe U = Unsafe.getUnsafe();
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

}

