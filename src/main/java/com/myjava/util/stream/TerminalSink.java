package com.myjava.util.stream;

import com.myjava.util.function.Supplier;

interface TerminalSink<T, R> extends Sink<T>, Supplier<R> {
}
