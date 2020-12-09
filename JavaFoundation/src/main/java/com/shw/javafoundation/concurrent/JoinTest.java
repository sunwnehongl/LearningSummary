package com.shw.javafoundation.concurrent;

/**
 * @Description TODO
 * @Author sunwenhong
 * @Date 2020/12/9 20:52
 */
public class JoinTest {
    private static int num = 0;
    public static void main(String[] args) {
        System.out.println("异步线程计算前的值:" + num);
        Thread thread = new Domino();
        thread.start();
        try {
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("异步线程计算失败");
        }
        System.out.println("异步线程计算后的值:" + num);
    }

    static class Domino extends Thread {
        public void run() {
            for (int i = 0; i < 100; i++) {
                num++;
            }
        }
    }
}
