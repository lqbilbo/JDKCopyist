package com.myjava.util.concurrent;

import java.util.concurrent.Future;

public interface RunnableFuture<V> extends Runnable, Future<V> {

    void run();
}
