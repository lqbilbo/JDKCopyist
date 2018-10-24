package com.myjava.nio;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.InvalidMarkException;
import java.util.Spliterator;

/**
 * 特定基础类型的数据容器
 * buffer是一个线性的，有限的特定基础类型元素的队列。buffer的重要属性是它的
 * capacity,limit和position
 *
 * <blockquote>
 *     <p>capacity是buffer包含元素的数量</p>
 *     <p>limit是第一个元素的索引，它不应该被读或者写入，一个buffer的limit永远不会
 *     是负数，而且不会超过capacity</p>
 *     <p>position是下一个读取或写入元素的索引。buffer的position永远不会是负数且不会
 *     大于limit</p>
 * </blockquote>
 *
 * <h2> 传输数据 </h2>
 * <p> 每个子类都会定义get和put操作</p>
 *
 * <blockquote>
 *     在当前position开始读或写的一个或更多的元素，然后增加position。
 *     如果请求的传输超过了limit那么get和put就会抛出{@link BufferUnderflowException}
 *     这样不会有数据被传输
 *     绝对操作获取一个外显的元素索引，不会影响position，绝对的get和put操作会发生
 *     {@link IndexOutOfBoundsException}如果index超过了limit。
 * </blockquote>
 *
 * <h2>标记和重置</h2>
 * <h2>不变型</h2>
 * <h2>清除、绕回和翻转</h2>
 * <h2>只读的buffer</h2>
 * <h2>线程安全</h2>
 * <h2>调用链</h2>
 *
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

    /**
     * 翻转buffer。limit别设置为当前的position，然后position被置为0.
     * 如果mark被定义那么它将会被丢弃
     * 在读channel或put操作之后，调用这个方法来准备写channel或者get操作
     *
     * 这个方法经常用来结合{@link ByteBuffer#compact()}方法当传输数据从一端到另一端。
     * @return
     */
    public final Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
    public final int remaining() {
        return limit - position;
    }

    public final boolean hasRemaining() {
        return position < limit;
    }

    public abstract boolean isReadOnly();

    public abstract boolean hasArray();

    public abstract Object array();

    public abstract int arrayOffset();

    public abstract boolean isDirect();

    final int nextGetIndex() {
        if (position >= limit)
            throw new BufferUnderflowException();
        return position++;
    }

    final int nextGetIndex(int nb) {
        if (limit - position < nb)
            throw new BufferUnderflowException();
        int p = position;
        position += nb;
        return p;
    }

    final int nextPutIndex() {                          // package-private
        if (position >= limit)
            throw new BufferOverflowException();
        return position++;
    }

    final int nextPutIndex(int nb) {                    // package-private
        if (limit - position < nb)
            throw new BufferOverflowException();
        int p = position;
        position += nb;
        return p;
    }

    final void truncate() {                             // package-private
        mark = -1;
        position = 0;
        limit = 0;
        capacity = 0;
    }
}
