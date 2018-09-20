package com.myjava.util.concurrent.atomic;

import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * 如果更新是具有线程争用的，那么这个方法{@link #sum()}可以返回目前总共的sum
 *
 * 这个类经常和{@link java.util.concurrent.atomic.AtomicLong}对比当多个线程
 * 更新一个通用的和，没有很好地考虑到同步控制。在低竞争下，两个类特征差不多，但是高
 * 竞争条件下，这个类的吞吐量将由于其空间的高效利用而更胜一筹。
 *
 * LongAdders可以用于{@link java.util.concurrent.ConcurrentHashMap}来保持一个
 * 可扩展的高频map。比如，增加一个count到freqs里，如果刚开始不存在元素时，你可以使用
 * freqs.computeIfAbsent(key, k -> new LongAdder()).increment();}
 */
public class LongAdder extends Striped64 implements Serializable {
    private static final long serialVersionUID = 7909175107192186494L;

    public LongAdder() {
    }

    public void add(long x) {
        Cell[] cs; long b,v; int m; Cell c;
        if ((cs = cells) != null || !casBase(b = base, b + x)) {
            boolean uncontended = true;
            if (cs == null || (m = cs.length - 1) < 0 ||
                (c = cs[getProbe() & m]) == null ||
                !(uncontended = c.cas(v = c.value, v + x)))
//                longAccumulate(x, null, uncontended);
            return;
        }
    }

    public void increment() {
        add(1L);
    }

    public void decrement() {
        add(-1L);
    }

    public long sum() {
        Cell[] cs = cells;
        long sum = base;
        if (cs != null) {
            for (Cell c : cs) {
                if (c != null)
                    sum += c.value;
            }
        }
        return sum;
    }

    public void reset() {}

    public long sumThenReset() {
        return 0L;
    }

    @Override
    public String toString() {
        return Long.toString(sum());
    }

    @Override
    public int intValue() {
        return (int) sum();
    }

    @Override
    public long longValue() {
        return sum();
    }

    @Override
    public float floatValue() {
        return (float)sum();
    }

    @Override
    public double doubleValue() {
        return (double)sum();
    }

    private static class SerializationProxy implements Serializable {
        private static final long serialVersionUID = 3320905235096618116L;

        private final long value;

        public SerializationProxy(LongAdder a) {
            this.value = a.sum();
        }

        private Object readResolve() {
            LongAdder a = new LongAdder();
            a.base = value;
            return a;
        }
    }

    private Object writeReplace() {
        return new SerializationProxy(this);
    }

    private void readObject(ObjectInputStream s) throws InvalidObjectException {
        throw new InvalidObjectException("Proxy required");
    }
}
