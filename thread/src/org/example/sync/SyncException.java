package org.example.sync;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class SyncException {
    public static void main(String[] args) {
        MySyncException exception = new MySyncException();
        new Thread(exception::test1, "t1").start();
        new Thread(exception::test2, "t2").start();
    }

    public static class MySyncException {

        public Unsafe getUnsafe() {
            try {
                Field field = Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                return (Unsafe) field.get(null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        public synchronized void test1() {
            try {
                System.out.println(Thread.currentThread().getName() + " running");
                Thread.sleep(2000);
                int i = 1 / 0;
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " exception");
                throw new RuntimeException(e);
            }
        }

        public synchronized void test2() {
            System.out.println(Thread.currentThread().getName() + " running");
        }
    }
}
