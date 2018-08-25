package com.myjava.util.concurrent;

import java.util.concurrent.Semaphore;

public class SemaphoreTest {

    private static class Pool {
        private static final int MAX_AVAILABLE = 100;
        private final Semaphore available = new Semaphore(MAX_AVAILABLE, true);

        public int getItem() throws InterruptedException {
            available.acquire();
            return getNextAvailableItem();
        }

        public void putItem(int x) {
            if (markAsUnused(x))
                available.release();
        }

        // Not a particularly efficient data structure; just for demo

        protected int[] items = new int[MAX_AVAILABLE];
        protected boolean[] used = new boolean[MAX_AVAILABLE];

        protected synchronized int getNextAvailableItem() {
            for (int i = 0; i < MAX_AVAILABLE; ++i) {
                if (!used[i]) {
                    used[i] = true;
                    return items[i];
                }
            }
            return 0;
        }

        protected synchronized boolean markAsUnused(int item) {
            for (int i = 0; i < MAX_AVAILABLE; ++i) {
                if (item == items[i]) {
                    if (used[i]) {
                        used[i] = false;
                        return true;
                    } else
                        return false;
                }
            }
            return false;
        }
    }

    public static void main(String[] args) {

        Pool pool = new Pool();
        Thread thread1 = new Thread(() -> {
            try {
                pool.getItem();
            } catch (InterruptedException e) {
                throw new RuntimeException();
            }
        });
        Thread thread2 = new Thread(() -> pool.putItem(10));
        thread2.start();
        thread1.start();
    }
}
