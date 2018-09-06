package com.myjava.util.stream;

import com.myjava.util.function.Consumer;

/**
 * Sink是{@link Consumer}的扩展往往用于指导在流管道的每个步骤中的数值，较于传统管理大小信息的方法，
 * 控制流等。在第一次调用{@code accept}之前，你必须先调用{@code begin()}方法来通知数据的到来
 * @param <T> value流的元素类型
 */
interface Sink<T> extends Consumer<T> {

    default void begin(long size) {}

    default void end() {}

    default boolean cancellationRequested() {
        return false;
    }

    default void accept(int value) {
        throw new IllegalStateException("called wrong accept method");
    }

    default void accept(long value) {
        throw new IllegalStateException("called wrong accept method");
    }

    default void accept(double value) {
        throw new IllegalStateException("called wrong accept method");
    }
}
