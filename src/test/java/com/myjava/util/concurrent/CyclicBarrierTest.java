package com.myjava.util.concurrent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
public class CyclicBarrierTest {

    private static class Solver {
        final int N;
        final float[][] data;
        final CyclicBarrier barrier;

        class Worker implements Runnable {
          int myRow;
          Worker(int row) { myRow = row; }
          public void run() {
            while (!done()) {
              processRow(myRow);

              try {
                barrier.await();
              } catch (InterruptedException | BrokenBarrierException ex) {
                return;
              }
            }
          }
        }
 
        public Solver(float[][] matrix) {
          data = matrix;
          N = matrix.length;
          Runnable barrierAction = () -> mergeRows();
          barrier = new CyclicBarrier(N, barrierAction);

          List<Thread> threads = new ArrayList<>(N);
          for (int i = 0; i < N; i++) {
            Thread thread = new Thread(new Worker(i));
            threads.add(thread);
            thread.start();
          }

            // wait until done
          try {
              for (Thread thread : threads) {
                  thread.join();
              }
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
        }

        private boolean done() {
            if (barrier.getNumberWaiting() == 0) return true;
            return false;
        }
        private void processRow(int row) {
            data[row][row] = row;
        }
        private void mergeRows() {
            for (int i = 0; i < N; i++)
                for (int j = 0; j < N; j++)
                    data[i][j] += N;
        }
    }

    public static void main(String[] args) {
    }
 
}
