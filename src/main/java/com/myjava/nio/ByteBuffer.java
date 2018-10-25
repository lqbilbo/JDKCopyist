package com.myjava.nio;

/**
 * 这个类在byte buffers的基础上定义了六种类型的操作
 *
 * 读和写单个字节的get()和put()方法
 *
 * 相对的get方法用来传输从buffer到数组的持续不断的字节
 *
 * 相对的put方法用来从字节数组或者其他字节中传递持续的字节
 *
 * getChar和putChar方法用来读取和写入基础类型数据的值，以一个特定的字节顺序传送他们
 *
 *
 * @author luoqi04
 * @version $Id: ByteBuffer.java, v 0.1 2018/10/25 下午9:59 luoqi Exp $
 */
public abstract class ByteBuffer
    extends Buffer
    implements Comparable<ByteBuffer> {

    final byte[] hb;
    final int offset;
    boolean isReadOnly;

    ByteBuffer(int mark, int pos, int lim, int cap,   // package-private
               byte[] hb, int offset)
    {
        super(mark, pos, lim, cap);
        this.hb = hb;
        this.offset = offset;
    }

    ByteBuffer(int mark, int pos, int lim, int cap) { // package-private
        this(mark, pos, lim, cap, null, 0);
    }
}
