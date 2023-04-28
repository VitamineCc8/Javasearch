package com.javasearch.www.util;

/**
 * @author VitamineG
 * @DateTime 2023/3/29 18:23
 */
public class Test {
    public static void main(String[] args) {
        Test test01 = new Test();
        Test test02 = new Test();

        new Thread(new Runnable() {
            @Override
            public void run() {
                test01.test();
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                test02.test();
            }
        }).start();

    }
    public  void  test(){
        synchronized(this){
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
