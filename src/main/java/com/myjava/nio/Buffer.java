package com.myjava.nio;

import java.nio.InvalidMarkException;
import java.util.Spliterator;

/**
 * @author luoqi04
 * @version $Id: Buffer.java, v 0.1 2018/10/23 上午7:38 luoqi Exp $
 */
public abstract class Buffer {

    static final int SPLITERATOR_CHARACTERISTICS =
            Spliterator.SIZED | Spliterator.SUBSIZED | Spliterator.ORDERED;

    // Invariants: mark <= position <= limit <= capacity
    private int mark = -1;
    private int position = 0;
    private int limit;
    private int capacity;

    //只有direct buffers使用
    long address;

    public Buffer(int mark, int pos, int lim, int cap) {        //package-private
        if (cap < 0)
            throw new IllegalArgumentException("Negative capacity: " + cap);
        this.capacity = cap;
        limit(lim);
        position(pos);
        if (mark >= 0) {
            if (mark > pos)
                throw new IllegalArgumentException("mark > position: ("
                                                        + mark + " > " + pos + ")");
            this.mark = mark;
        }
    }

    public final int capacity() {
        return capacity;
    }

    public final int position() {
        return position;
    }

    /**
     * 设置buffer的limit。如果position大于最新的limit那么会被设置为新的limit。
     * 如果mark被定义且大于新的limit，那么它将会被丢弃
     * @param newLimit
     * @return
     */
    public final Buffer limit(int newLimit) {
        if ((newLimit > capacity) || (newLimit < 0))
            throw new IllegalArgumentException();
        limit = newLimit;
        if (position > limit) position = limit;
        if (mark > limit) mark = -1;
        return this;
    }

    public final int limit() {
        return limit;
    }

    /**
     * 设置buffer的position。如果mark被定义且大于最新的position，将会被丢弃
     * @param newPosition
     * @return
     */
    public final Buffer position(int newPosition) {
        if ((newPosition > limit) || (newPosition < 0))
            throw new IllegalArgumentException();
        position = newPosition;
        if (mark > position) mark = -1;
        return this;
    }

    public final Buffer mark() {
        mark = position;
        return this;
    }

    public final Buffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
    }

    /**
     * 清空buffer。position被设置成0，limit被设置成capacity，mark被丢弃。
     *
     * 在channel读取队列或者填充buffer的put操作之前调用这个方法。
     *
     * <blockquote><pre>
     * buf.clear();     //准备读取的buffer
     * in.read(buf);    //读取数据</pre></blockquote>
     *
     * <p> 这个方法不会真正擦除buffer中的数据，但是它被这样命名是由于
     * 经常用于这样的场景</p>
     *
     * @return
     */
    public final Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
}
