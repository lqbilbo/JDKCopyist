package com.myjava.util.stream;

import com.myjava.util.function.Consumer;

import java.util.Objects;

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

    /**
     * 创建sinks的链条。{@code begin},{@code end}和{@code cancelRequested}方法
     * 被downstream的链条绑缚上。这个实现把不可知的输入传输给downstream并且产生一个
     * {@code Sink<T>}。{@code accepts()}方法的实现必须在下游{@code Sink}上调用
     * 正确的{@code accept()}
     * @param <T>
     * @param <E_OUT>
     */
    abstract static class ChainedReference<T, E_OUT> implements Sink<T> {
        protected final Sink<? super E_OUT> downstream;

        public ChainedReference(Sink<? super E_OUT> downstream) {
            this.downstream = Objects.requireNonNull(downstream);
        }

        @Override
        public void begin(long size) {
            downstream.begin(size);
        }

        @Override
        public void end() {
            downstream.end();
        }

        @Override
        public boolean cancellationRequested() {
            return downstream.cancellationRequested();
        }
    }
}
