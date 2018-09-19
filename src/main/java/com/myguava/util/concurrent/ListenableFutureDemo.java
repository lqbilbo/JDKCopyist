package com.myguava.util.concurrent;

import com.google.common.util.concurrent.*;
import com.google.common.util.concurrent.ListenableFuture;
import com.myjava.util.concurrent.Executors;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

/**
 * @author luoqi04
 * @version $Id: ListenableFutureDemo.java, v 0.1 2018/9/18 下午3:50 luoqi Exp $
 */
public class ListenableFutureDemo {

    private static final int POOL_SIZE = 50;

    private static final ListeningExecutorService service = MoreExecutors
            .listeningDecorator(Executors.newFixedThreadPool(POOL_SIZE));

    private static Logger LOG = LoggerFactory.getLogger(ListenableFutureDemo.class);

    @Test
    public void testListenableFuture() {
        final List<String> value = Collections.synchronizedList(new ArrayList<String>());
        try {
            List<ListenableFuture<String>> futures = new ArrayList<ListenableFuture<String>>();
            for (int i = 0; i < 10; i++) {
                final int index = i;
                if (i == 9) {
                    Thread.sleep(500 * i);
                }
                ListenableFuture<String> sfuture = service
                    .submit(new Callable<String>() {
                        public String call() throws Exception {
                            long time = System.currentTimeMillis();
                            LOG.info("Finishing sleeping task{}: {}", index, time);
                            return String.valueOf(time);
                        }
                    });
                sfuture.addListener(new Runnable() {
                    public void run() {
                        LOG.info("Listener be triggered for task{}.", index);
                    }
                }, service);

                Futures.addCallback(sfuture, new FutureCallback<String>() {
                    public void onSuccess(String result) {
                        LOG.info("Add result value into value list {}.", result);
                        value.add(result);
                    }

                    public void onFailure(Throwable t) {
                        LOG.info("Add result value into value list error.", t);
                        throw new RuntimeException(t);
                    }
                });
                futures.add(sfuture);
            }

            ListenableFuture<List<String>> allAsList = Futures.allAsList(futures);
            allAsList.get();
            LOG.info("All sub-task are finished.");
        } catch (Exception ignored) {

        }
    }

    @Test
    public void testCompletableFuture() throws Exception {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            LOG.info("Run supplyAsync");
            return "Return result of Supply Async";
        });

        future.thenRun(new Runnable() {
            @Override
            public void run() {
                LOG.info("Run action.");
            }
        });

        future.thenRunAsync(new Runnable() {
            @Override
            public void run() {
                LOG.info("Run async action.");
            }
        });

        future.whenComplete((v, e) -> {
            LOG.info("WhenComplete value: " + v);
            LOG.info("WhenComplete exception: " + e);
        });
        CompletableFuture<Void> future2 = CompletableFuture.runAsync(() -> {
            LOG.info("Return result of Run Async.");
        });

        CompletableFuture<String> future3 = CompletableFuture.supplyAsync(() -> {
            return "hello";
        });

        CompletableFuture<String> future4 = CompletableFuture.supplyAsync(() -> {
            return "world";
        });

        CompletableFuture<String> f = future3.thenCombine(future4, (x,y) -> x + "-" + y);
        LOG.info(f.get());
    }
}
