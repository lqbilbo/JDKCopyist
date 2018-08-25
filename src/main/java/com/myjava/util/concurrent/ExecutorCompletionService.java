package com.myjava.util.concurrent;

import java.util.concurrent.*;

/**
 * {@link CompletionService} 使用 {@link Executor} 来执行任务。这个类通过将完成的任务
 * 编排在队列中，通过take来获取。当处理一组任务时这个类时很轻量级而且适合短暂使用。
 *
 */
public abstract class ExecutorCompletionService<V> implements CompletionService<V> {

    private final Executor executor;
    private final AbstractExecutorService aes;
    private final java.util.concurrent.BlockingQueue<Future<V>> completionQueue;

    private static class QueueingFuture<V> extends FutureTask<Void> {
        QueueingFuture(RunnableFuture<V> task,
                       BlockingQueue<Future<V>> completionQueue) {
            super(task, null);
            this.task = task;
            this.completionQueue = completionQueue;
        }
        private final Future<V> task;
        private final BlockingQueue<Future<V>> completionQueue;
        protected void done() { completionQueue.add(task); }
    }

    public ExecutorCompletionService(Executor executor) {
        if (executor == null) throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
                (AbstractExecutorService) executor : null;
        this.completionQueue = new LinkedBlockingDeque<>();
    }

    public ExecutorCompletionService(Executor executor, java.util.concurrent.BlockingQueue<Future<V>> completionQueue) {
        if (executor == null || completionQueue == null) throw new NullPointerException();
        this.executor = executor;
        this.aes = (executor instanceof AbstractExecutorService) ?
                (AbstractExecutorService) executor : null;
        this.completionQueue = completionQueue;
    }

    private java.util.concurrent.RunnableFuture<V> newTaskFor(Callable<V> task) {
        if (aes == null) {
            return new FutureTask<>(task);
        }
        //FIXME
        return null;
        /*else
            return aes.newTaskFor(task);*/
    }

    /*private RunnableFuture<V> newTaskFor(Runnable task, V result) {
        if (aes == null)
            return new FutureTask<>(task, result);
        else
            return aes.newTaskFor(task, result);
    }

    @Override
    public Future<V> submit(Callable<V> task) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task);
        executor.execute(new QueueingFuture<>(f, completionQueue));
        return f;
    }*/

    /*@Override
    public Future<V> submit(Runnable task, V result) {
        if (task == null) throw new NullPointerException();
        RunnableFuture<V> f = newTaskFor(task, result);
        executor.execute(new QueueingFuture<>(f, completionQueue));
        return f;
    }*/

    @Override
    public Future<V> take() throws InterruptedException {
        return completionQueue.take();
    }

    @Override
    public Future<V> poll() {
        return completionQueue.poll();
    }

    @Override
    public Future<V> poll(long timeout, TimeUnit unit) throws InterruptedException {
        return completionQueue.poll(timeout, unit);
    }
}
