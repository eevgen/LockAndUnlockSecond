package org.example;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.IntConsumer;
import java.util.stream.IntStream;

import static java.lang.Thread.*;
import static java.util.stream.IntStream.*;

public class Main {

    private static final Counter counter = new Counter();

    public static void main(String[] args) throws InterruptedException{


        Thread incrementingThread = new Thread(counter.calculate(10, i -> counter.increment(), counter));
        Thread decrementingThread = new Thread(counter.calculate(10, i -> counter.decrement(), counter));

        startingThreads(incrementingThread, decrementingThread);
        joiningThreads(incrementingThread,decrementingThread);

        System.out.printf("Final Balance: %d\n", counter.balance);
    }

    public static void startingThreads(Thread... threads) {
        Arrays.stream(threads).forEach(Thread::start);
    }

    public static void joiningThreads(Thread... threads) throws InterruptedException{
        for(Thread thread : threads) {
            thread.join();
        }
    }

    public static class Counter {

        private int balance = 0;
        private Lock lock = new ReentrantLock();
        private void lock() {
            lock.lock();
            System.out.printf("Thread: '%s' is turning on lock\n", currentThread().getName());
        }

        private void unlock() {
            lock.unlock();
            System.out.printf("Thread: '%s' is turning off lock\n", currentThread().getName());
        }

        private Runnable calculate(int repeat, IntConsumer process, Counter counter) {
            return () -> {
                lock();
                try {
                    range(0, repeat).forEach(process);
                } finally {
                    unlock();
                }
            };
        }

        public int increment() {
            System.out.printf("Thread: '%s' has incremented balance, Balance: %d\n", currentThread().getName(), balance);
            return balance++;
        }

        public int decrement() {
            System.out.printf("Thread: '%s' has decremented balance, Balance: %d\n", currentThread().getName(), balance);
            return balance--;
        }
    }
}