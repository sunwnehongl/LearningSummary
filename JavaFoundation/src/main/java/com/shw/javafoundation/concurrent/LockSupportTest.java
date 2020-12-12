package com.shw.javafoundation.concurrent;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 * @Description LockSupport 代码联系
 * @Author sunwenhong
 * @Date 2020/12/12 15:50
 */
public class LockSupportTest {

    private static void park_test() {
        Thread thread = new Thread(() -> {
            System.out.println("我马上要睡觉了！");
            LockSupport.park();
            System.out.println("我被唤醒了！");
        });
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        thread.interrupt();
        // LockSupport.unpark(thread);
    }

    private static void parkNanos_test() {
        Thread thread = new Thread(() -> {
            System.out.println("我马上要睡觉了！");
            LockSupport.parkNanos(TimeUnit.SECONDS.toNanos(1));
            System.out.println("我被唤醒了！");
        });
        thread.start();
    }

    private static void parkUntil_test() {
        long now = System.currentTimeMillis();
        Thread thread = new Thread(() -> {
            System.out.println("我马上要睡觉了！");
            LockSupport.parkUntil(now + TimeUnit.SECONDS.toMillis(2));
            System.out.println("我被唤醒了！");
        });
        thread.start();
    }

    public static void main(String[] args) {
        park_test();
        parkNanos_test();
        parkUntil_test();
    }
}
