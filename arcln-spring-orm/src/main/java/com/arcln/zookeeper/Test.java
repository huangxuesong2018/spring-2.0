package com.arcln.zookeeper;

import java.io.IOException;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;

public class Test {
    public static void main(String[] args) throws IOException {
        final CountDownLatch countDownLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        countDownLatch.await();
                        DistributedLock distributedLock = new DistributedLock();
                        distributedLock.lock();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            },"Thread-"+i).start();
            countDownLatch.countDown();
        }
       // System.in.read();
    }
}
