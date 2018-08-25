package com.myjava.util.concurrent;

import java.util.concurrent.ScheduledFuture;

public interface RunnableScheduledFuture<V> extends RunnableFuture<V>, ScheduledFuture<V> {

    boolean isPeriodic();
}