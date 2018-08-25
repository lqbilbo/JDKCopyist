package com.myjava.util.concurrent;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 通过消耗已完成任务的结果来产生新的异步任务的服务。
 * CompletionService用于管理异步I/O，读取的任务在系统的一个部分提交，而在另一个
 * 部分完成读取，可能和他们请求的顺序不同。
 *
 * {@link ExecutorCompletionService} 提供了实现
 * @param <V>
 */
public interface CompletionService<V> {

    Future<V> submit(Callable<V> task);

    Future<V> submit(Runnable task, V result);

    Future<V> take() throws InterruptedException;

    Future<V> poll();

    Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException;

}
