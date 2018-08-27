package com.myjava.util.stream;

import java.util.Iterator;
import java.util.Spliterator;

/**
 * streams的基类接口，它是不仅支持串行还支持并行聚合操作的元素的集合。下面这个例子展示了一个使用了
 * Stream和IntStream的聚合操作，计算red widgets的weight的总和
 *
 * {@code
 *      int sum = widgets.stream()
 *                       .filter(w -> w.getColor()==RED
 *                       .mapToInt(w -> w.getWeight())
 *                       .sum();
 * }
 */
public interface BaseStream<T, S extends BaseStream<T, S>>
        extends AutoCloseable {

    Iterator<T> iterator();

    Spliterator<T> spliterator();

    boolean isParallel();

    S sequential();

    S parallel();

    S unordered();

    S onClose(Runnable closeHandler);

    @Override
    void close();
}
