package com.myjava.util.concurrent;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    
      static class Driver {
        private final int N = 10;
        void main() throws InterruptedException {
          CountDownLatch startSignal = new CountDownLatch(1);
          CountDownLatch doneSignal = new CountDownLatch(N);

          for (int i = 0; i < N; ++i) // create and start threads
            new Thread(new Worker(startSignal, doneSignal)).start();

          System.out.println(Thread.currentThread().getName());           // don't let run yet
          startSignal.countDown();      // let all threads proceed
          System.out.println(Thread.currentThread().getName());
          doneSignal.await();           // wait for all to finish
        }
      }
 
      static class Worker implements Runnable {
        private final CountDownLatch startSignal;
        private final CountDownLatch doneSignal;
        Worker(CountDownLatch startSignal, CountDownLatch doneSignal) {
          this.startSignal = startSignal;
          this.doneSignal = doneSignal;
        }
        public void run() {
          try {
            startSignal.await();
            doWork();
            doneSignal.countDown();
          } catch (InterruptedException ex) {} // return;
        }

        void doWork() {  }
      }
    
}
