package com.myjava.util.stream;

import com.myjava.util.function.Consumer;

import java.util.Objects;

/**
 * Sink是{@link Consumer}的扩展往往用于指导在流管道的每个步骤中的数值，较于传统管理大小信息的方法，
 * 控制流等。在第一次调用{@code accept}之前，你必须先调用{@code begin()}方法来通知数据的到来，待
 * 所有的数据传送之后，你必须调用{@code end()}方法结束。在这之后你只有先调用{@code begin()}才能
 * 再次调用{@code accept()}。{@code Sink}也提供了一种协作信号机制，用于在不打算接收数据时调用的
 * {@code cancellationRequested()}，这样一个source就可以在把更多数据发送给{@code Sink}之前取出。
 *
 * 一个sink只有存在于两种状态中的一种：初始状态和激活状态。它开始于起始状态{@code begin()}方法可以将
 * 其状态设置为激活，{@code end()}方法会将其转换为初始状态，这样达到了重用的目的。Data-accepting方法
 * (比如{@code accept})只有在激活状态下才有效。
 *
 * 一个流管道包含了一个source，多个(也可能没有)中间步骤和最终的步骤像reduction或for-each。
 *
 * {@code Sink}实例用于表达管道的每个步骤，这个步骤可能接收到objects,ints,longs,doubles。Sink并不需要
 * 一个特定的接口来对待基础类型。（它可能被叫做"kitchen sink"来表明兴趣趋势.）{@code Sink}实现了给定步骤
 * 的组合被期望了解到下一个步骤的数据类型，在下游的{@code Sink}上调用正确的{@code accept()}方法。类似的，
 * 每个步骤都必须实现正确的符合它可以接收的数据类型的{@code accept}方法。
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
