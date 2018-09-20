package com.myjava.util.concurrent.atomic;

import sun.misc.Unsafe;

import java.io.Serializable;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

/**
 * @author luoqi04
 * @version $Id: AtomicReference.java, v 0.1 2018/9/20 上午7:42 luoqi Exp $
 */
public class AtomicReference<V> implements Serializable {

    private static final long serialVersionUID = -2640151739075212071L;

    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset(
                    AtomicReference.class.getDeclaredField("value")
            );
        } catch (Exception ex) {
            throw new Error(ex);
        }
    }

    private volatile V value;

    public AtomicReference(V initialValue) {
        this.value = initialValue;
    }

    public AtomicReference() {
    }

    public final V get() { return value; }

    public final void set(V newValue) { value = newValue; }

    public final void lazySet(V newValue) {
        unsafe.putOrderedObject(this, valueOffset, newValue);
    }

    public final boolean compareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    public final boolean weakCompareAndSet(V expect, V update) {
        return unsafe.compareAndSwapObject(this, valueOffset, expect, update);
    }

    @SuppressWarnings("unchecked")
    public final V getAndSet(V newValue) {
        return (V) unsafe.getAndSetObject(this, valueOffset, newValue);
    }

    /**
     * 自动更新现有的值为应用了给定的function的结果，返回先前的值。这个function应当是
     * 无副作用的，由于会在尝试更新时再次应用，因此会在线程之间竞争
     * @param updateFunction
     * @return
     */
    public final V getAndUpdate(UnaryOperator<V> updateFunction) {
        V prev, next;
        do {
            prev = get();
            next = updateFunction.apply(prev);
        } while (!compareAndSet(prev, next));
        return prev;
    }

    public final V getAndAccumulate(V x,
                                    BinaryOperator<V> accumulatorFunction) {
        V prev, next;
        do {
            prev = get();
            next = accumulatorFunction.apply(prev, x);
        } while (!compareAndSet(prev, next));
        return next;
    }

    @Override
    public String toString() {
        return "AtomicReference{" +
                "value=" + value +
                '}';
    }
}
