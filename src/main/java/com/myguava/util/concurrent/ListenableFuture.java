package com.myguava.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * @author luoqi04
 * @version $Id: ListenableFuture.java, v 0.1 2018/9/18 下午2:48 luoqi Exp $
 *
 * 可接受完成监听器的{@link Future}。每个监听器都有一个相关联的executor，当future的计算通过
 * {@linkplain Future#isDone()}完成的时候会调用这个executor。如果当监听器添加时计算已经完成，
 * 那么监听器则会立即执行execute。
 *
 * {@code ListenableFuture}的主要意图是帮助你讲一系列的异步操作通过链条串起来。你可以通过调用
 * 类似于{@link com.google.common.util.concurrent.Futures#transform(com.google.common.util.concurrent.ListenableFuture, Function, Executor) Futures.transform}，但是往往你会发现使用一个框架比较容易。框架会自动化
 * 整个进程，并且增加一些诸如监控，调试和逆向补偿机制。
 *
 * 官方鼓励在方法中返回{@code ListenableFuture}，这样你的使用者就可以从中获取到和Futures相关
 * 的特性带来的好处。方法是创建一个{@code ListenableFuture}实例，它依赖于你创建的{@code Future}实例。
 *
 * 1 如果你从{@code ExecutorService}中接收数据，将其转换成{@link com.google.common.util.concurrent.ListeningExecutorService},
 * 通过调用{@link com.google.common.util.concurrent.MoreExecutors#listeningDecorator(ExecutorService)}
 * 2 如果你是通过{@link java.util.concurrent.FutureTask#set(Object)}或者类似的方法，
 * 创建一个{@link com.google.common.util.concurrent.SettableFuture}代替。如果你的需求很复杂，你
 * 可能需要{@link com.google.common.util.concurrent.AbstractFuture}
 *
 */
@GwtCompatible
public interface ListenableFuture<V> extends Future<V> {

    /**
     * 注册listener在给定的executor上执行{@linkplain Executor#execute(Runnable) run}。这个
     * listener会在{@code Future}的计算{@linkplain Future#isDone()}完成的时候运行。如果计算已经
     * 完成，那么就会立即执行。
     * 没有确保的执行顺序，然而通过这个方法添加的任何listener可以保证计算完成的时候立马调用。
     * 被listener抛出的异常会传递给执行者。在{@code Executor.execute}期间抛出的异常，比如
     * {@code RejectedExecutionException}或者被{@linkplain MoreExecutors#directExecutor()}抛出
     * 的异常会立即抓住并记录日志。
     * 记住：为了快捷，轻量级的listener在任何线程中执行时都是安全的，可以看下{@link MoreExecutors#directExecutor()}.
     * 然而，重量级的{@code directExecutor}listeners会导致问题，这些问题很难暴露出来因为它们依赖于时间。
     * 比如：
     * listener通过{@code addListener}方法的调用被执行。调用者可能是UI线程或者其他队延迟比较敏感的线程。
     * 这个会影响UI的响应性。listener会通过完成{@code Future}的线程来执行。这个线程如果是类似RPC网络线程
     * 这样的内部系统线程。阻塞线程会影响整个系统的进程。它甚至会造成死锁。
     * listener会导致其他监听器的延迟，甚至不是{@code directExecutor}监听.
     *
     * 这就是最常用的listener接口。更加通用的监听器操作可以看下{@link com.google.common.util.concurrent.Futures}。
     * 如果想看简化版的listener接口，看下{@link com.google.common.util.concurrent.Futures#addCallback(com.google.common.util.concurrent.ListenableFuture, FutureCallback)}
     *
     *  @param listener 当计算完成时运行的listener
     * @param executor 监听器的执行者
     */
    void addListener(Runnable listener, Executor executor);
}
