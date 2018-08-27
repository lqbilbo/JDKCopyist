package com.myjava.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {

    boolean test(T t);

    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return (t) -> test(t) && other.test(t);
    }

    /**
     * 返回一个猜测的否定
     * @return
     */
    default Predicate<T> negate() {
        return t -> !test(t);
    }

    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return t -> test(t) || other.test(t);
    }

    static <T> Predicate isEqual(Object targetRef) {
        return (null == targetRef)
                ? Objects::isNull
                : o -> targetRef.equals(o);
    }
}
