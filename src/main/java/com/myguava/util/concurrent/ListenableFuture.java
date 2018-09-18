package com.myguava.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;

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

    void addListener(Runnable listener, Executor executor);
}
