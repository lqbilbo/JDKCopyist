package com.myjava.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;

public class ExecutorServiceTest {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        ExecutorService executorService = Executors.newWorkStealingPool();
        List<TaskSleep> callList = new ArrayList<>();
        for (int i = 0; i < 2000; i++) {
            callList.add(new TaskSleep(i));
        }

        int sum = 0;
        /*for (int i = 0; i < callList.size(); i++) {
            sum += callList.get(i).num;
        }*/
        List<Future<Integer>> futures = executorService.invokeAll(callList);
        executorService.shutdown();
        for (Future<Integer> item : futures) {
            sum += item.get();
        }
        System.out.println("result: " + sum);
        long end = System.currentTimeMillis();
        System.out.println((double) (end - start) / 1000);
    }

    static class TaskSleep implements Callable<Integer> {

        private int num;

        public TaskSleep(int num) {
            this.num = num;
        }

        @Override
        public Integer call() throws Exception {
            Thread.sleep(1000);
            return num;
        }
    }
}
