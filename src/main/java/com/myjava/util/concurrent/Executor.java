package com.myjava.util.concurrent;

@FunctionalInterface
public interface Executor {

    void execute(Runnable command);
}
