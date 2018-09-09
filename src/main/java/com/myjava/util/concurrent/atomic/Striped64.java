package com.myjava.util.concurrent.atomic;

import jdk.internal.vm.annotation.Contended;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.security.PrivilegedAction;
import java.util.function.DoubleBinaryOperator;

/**
 * 支持动态的64位数据类型。
 */
@SuppressWarnings("serial")
public abstract class Striped64 extends Number {

    /*
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     *
     */

    @Contended static final class Cell {
        volatile long value;

        public Cell(long x) {
            this.value = x;
        }
        final boolean cas(long cmp, long val) {
            return VALUE.compareAndSet(this, cmp, val);
        }
        final void reset() {
            VALUE.setVolatile(this, 0L);
        }
        final void reset(long identity) {
            VALUE.setVolatile(this, identity);
        }
        final long getAndSet(long val) {
            return (long) VALUE.getAndSet(this, val);
        }

        private static final VarHandle VALUE;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                VALUE = l.findVarHandle(Cell.class, "value", long.class);
            } catch (ReflectiveOperationException e) {
                throw new Error(e);
            }
        }

    }
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    transient volatile Cell[] cells;
    transient volatile long base;
    transient volatile int cellsBusy;
    Striped64() {
    }

    final boolean casBase(long cmp, long val) {
        return BASE.compareAndSet(this, cmp, val);
    }

    final long getAndSetBase(long val) {
        return (long) BASE.getAndSet(this, val);
    }

    final boolean casCellsBusy() {
        return CELLSBUSY.compareAndSet(this, 0, 1);
    }

    static final int getProbe() {
        return (int) THREAD_PROBE.get(Thread.currentThread());
    }

    static final int advanceProbe(int probe) {
        probe ^= probe << 13;
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        THREAD_PROBE.set(Thread.currentThread(), probe);
        return probe;
    }

    private static long apply(DoubleBinaryOperator fn, long v, double x) {
        double d = Double.longBitsToDouble(v);
        d = (fn == null) ? d + x : fn.applyAsDouble(d, x);
        return Double.doubleToRawLongBits(d);
    }

    private static final VarHandle BASE;
    private static final VarHandle CELLSBUSY;
    private static final VarHandle THREAD_PROBE;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            BASE = l.findVarHandle(Striped64.class,
                    "base", long.class);
            CELLSBUSY = l.findVarHandle(Striped64.class,
                    "cellsBusy", int.class);
            l = java.security.AccessController.doPrivileged(
                    (PrivilegedAction<MethodHandles.Lookup>) () -> {
                        try {
                            return MethodHandles.privateLookupIn(Thread.class, MethodHandles.lookup());
                        } catch (ReflectiveOperationException e) {
                            throw new Error(e);
                        }
                    }
            );
            THREAD_PROBE = l.findVarHandle(Thread.class, "threadLocalRandomProbe", int.class);
        } catch (ReflectiveOperationException e) {
            throw new Error(e);
        }
    }
}
