package com.myjava.util.stream;

import java.util.stream.*;

interface TerminalOp<E_IN, R> {

    default StreamShape inputShape() {
        return StreamShape.REFERENCE;
    }

    default int getOpFlags() {
        return 0;
    }

    enum StreamShape {
        /**
         * The shape specialization corresponding to {@code Stream} and elements
         * that are object references.
         */
        REFERENCE,
        /**
         * The shape specialization corresponding to {@code IntStream} and elements
         * that are {@code int} values.
         */
        INT_VALUE,
        /**
         * The shape specialization corresponding to {@code LongStream} and elements
         * that are {@code long} values.
         */
        LONG_VALUE,
        /**
         * The shape specialization corresponding to {@code DoubleStream} and
         * elements that are {@code double} values.
         */
        DOUBLE_VALUE
    }
}
