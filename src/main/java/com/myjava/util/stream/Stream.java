package com.myjava.util.stream;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.*;
import java.util.stream.*;

/**
 * 支持串行和并行聚合操作的一系列元素。
 *
 * {@Code
 *      int sum = Widgets.stream()
 *                       .filter(w -> w.getColor() == RED)
 *                       .mapToInt(w -> w.getWeight())
 *                       .sum();
 * }
 *
 * 在这个例子中，{@code widgets}是一个{@code Collection<Widget>}。我们可以通过{@link Collection#stream()}
 * 创建，过滤它并产生一个只包含红色widgets的流，然后转换为{@code int}表示每个红色widgets的权重。然后计算总权重。
 *
 * 一个stream pipeline由source组成，这个source可能是一个array，一个collection，一个生成函数或者I/O channel。
 * 也可能会包含中间操作（将一个stream转换为另一个，比如
 * {@link java.util.stream.Stream#filter(java.util.function.Predicate)}）,
 * 和最终操作（产生一个结果或副作用，比如{@link java.util.stream.Stream#count()}或
 * {@link java.util.stream.Stream#forEach(Consumer)}.
 * Streams是懒加载的，即作用于source data上的计算只有当最终操作初始化完成之后才会起作用，或者source elements需要时
 * 才进行消费。
 *
 * Collections和Stream虽然维持着表面上的相似性，然而目标却不同。Collections旨在高效管理，访问他们的元素。相反，streams
 * 关注的是source和计算操作的可描述性，然而，如果提供的流操作不能提供定义的函数性。
 *
 * stream pipeline，就像上面"widgets"例子所说的那样，可以被视为在流数据源的查询。除非数据源现实被设计成并发可修改
 * (就像{@link com.myjava.util.concurrent.ConcurrentMap}一样)，那么在流的遍历过程中进行修改会导致不可预测或者错误的行为。
 *
 * 大多数情况下流操作都被设计成无状态的（它的意思是指运算出的结果不依赖于在stream pipeline执行过程中修改的状态）
 *
 * 流不可复用。如果检测到stream被重复使用，那么实现类就会抛出{@link IllegalStateException}。
 *
 * Streams有一个{@link java.util.stream.Stream#close()}方法，实现了{@link AutoCloseable}.在流关闭之后进行的操作也会抛出{@link IllegalStateException}
 * 尽管stream实例通常并不需要特殊的资源管理。仅当streams的数据源是一个IO管道，比如{@link java.nio.file.Files#lines(Path)},它确实
 * 需要关闭之外。如果一个流需要closing，它必须通过try-with-resources进行打开，或者类似的控制结构，来确保在操作完成时进行刻意地关闭。
 *
 * Stream pipelines可以串行也可以并行执行。这个执行模式是stream的属性。Streams通过选择sequential或parallel来决定。可以通过方法
 * {@link java.util.stream.Stream#isParallel()}检测
 */
public interface Stream<T> extends BaseStream<T, Stream<T>> {

    Stream<T> filter(Predicate<? super T> predicate);

    <R> Stream<R> map(Function<? super T, ? extends R> mapper);

    IntStream mapToInt(ToIntFunction<? super T> mapper);

    LongStream mapToLong(ToLongFunction<? super T> mapper);

    DoubleStream mapToDouble(ToDoubleFunction<? super T> mapper);

    /**
     * 举例来说，如果{@code orders}是购物订单的stream，每个购物订单包含了商品的集合，下面这个操作产生了一个
     * 包含了订单中所有商品的stream
     * {@code
     *      orders.flatMap(order->order.getLineItems().stream()
     * }
     * 再举一个例子，如果{@code path}是一个文件的路径，下面这个操作就产生了一文件中包含的所有{@code words}的stream
     * {@code
     *      Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8);
     *      Stream<String> words = lines.flatMap(line->Streams.of(line.split(" +")))
     * }
     * @param mapper
     * @param <R>
     * @return
     */
    <R> Stream<R> flatMap(Function<? super T, ? extends Stream<? extends R>> mapper);

    IntStream flatMapToInt(Function<? super T, ? extends IntStream> mapper);

    LongStream flatMapToLong(Function<? super T, ? extends LongStream> mapper);

    DoubleStream flatMapToDouble(Function<? super T, ? extends DoubleStream> mapper);

    Stream<T> distinct();

    Stream<T> sorted();

    Stream<T> sorted(Comparator<? super T> comparator);

    Stream<T> peek(Consumer<? super T> action);

    Stream<T> limit(long maxSize);

    Stream<T> skip(long n);

    /**
     * 非线程安全，尽量不用
     * @param action
     */
    void forEach(Consumer<? super T> action);

    void forEachOrdered(Consumer<? super T> action);

    Object[] toArray();

    <A> A[] toArray(IntFunction<A[]> generator);

    T reduce(T identity, BinaryOperator<T> accumulator);

    Optional<T> reduce(BinaryOperator<T> accumulator);

    <U> U reduce(U identity,
                 BiFunction<U, ? super T, U> accumulator,
                 BinaryOperator<U> combiner);

    <R> R collect(Supplier<R> supplier,
                  BiConsumer<R, ? super T> accumulator,
                  BiConsumer<R, R> combiner);

    <R, A> R collect(Collector<? super T, A, R> collector);

    Optional<T> min(Comparator<? super T> comparator);

    Optional<T> max(Comparator<? super T> comparator);

    long count();

    boolean anyMatch(Predicate<? super T> predicate);

    boolean allMatch(Predicate<? super T> predicate);

    boolean noneMatch(Predicate<? super T> predicate);

    Optional<T> findFirst();

    Optional<T> findAny();

}
