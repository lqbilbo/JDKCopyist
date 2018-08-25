package com.myjava.util.concurrent;

/**
 * 用于在生产者-消费者模式中发布流控制组件，每个都被Subscription管理
 *
 * 它可以应用在并发和分布式异步配置中：所有方法都定义于"one-way"消息风格。
 *
 * 基于Subscription#request方式的通信可以用于避免在push系统中发生资源管理问题
 */
public final class Flow {

    private Flow() {}

    @FunctionalInterface
    public static interface Publisher<T> {

        public void subscribe(Subscriber<? super T> subscriber);
    }

    public static interface Subscriber<T> {

        public void onSubscribe(Subscription subscription);

        public void onNext(T item);

        public void onError(Throwable throwable);

        public void onComplete();
    }

    public static interface Subscription {

        public void request(long n);

        public void cancel();
    }

    public static interface Processor<T, R> extends Subscriber<T>, Publisher<R> {

    }

    static final int DEFAULT_BUFFER_SIZE = 256;

    public static int defaultBufferSize() {
        return DEFAULT_BUFFER_SIZE;
    }
}
