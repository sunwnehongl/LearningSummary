package com.shw.javafoundation.concurrent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Description TODO
 * @Author sunwenhong
 * @Date 2020/12/11 21:58
 */
public class LockTest {

    private static Map<String, String> map = new HashMap<>();

    /**
     * lock 和unlock必需成对
     */
    public static void testRepeatLock() {
        Lock lock = new ReentrantLock();
        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                lock.lock();
                System.out.println(Thread.currentThread().getName() + "获取到锁");
                lock.unlock();
                lock.unlock();
            }
        }).start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + ":获取到锁");
                lock.unlock();
            }
        }).start();
    }

    /**
     * 读写锁测试
     */
    private static void testReadWriteLock() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        new Thread(() -> {
            readWriteLock.writeLock().lock();
            map.put("1", "1");
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.writeLock().unlock();
        }).start();

        new Thread(() -> {
            readWriteLock.readLock().lock();
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.readLock().unlock();
        }).start();

        new Thread(() -> {
            readWriteLock.writeLock().lock();
            map.put("2", "2");
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.writeLock().unlock();
        }).start();

    }

    /**
     * 锁降级使用
     */
    private static void lockUpgradeTest() {
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        new Thread (() -> {
            readWriteLock.writeLock().lock();
            map.put("3", "3");
            readWriteLock.readLock().lock();
            readWriteLock.writeLock().unlock();
            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.readLock().unlock();

        }).start();

        new Thread(() -> {
            readWriteLock.readLock().lock();
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.readLock().unlock();
        }).start();

        new Thread (() -> {
            readWriteLock.writeLock().lock();
            map.put("4", "4");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + ": " + map.toString());
            readWriteLock.writeLock().unlock();


        }).start();
    }

    public static void main(String[] args) {
        //testRepeatLock();
        //testReadWriteLock();
        lockUpgradeTest();
    }
}
