package org.example.thread.thread_design_pattern.single_threaded_execution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SafeList {
    public static void main(String[] args) {
        // safeArrayList();
        safeCopyOnWriteList();
    }

    public static void  safeArrayList() {
        List<Integer> list = new ArrayList<>();
        List<Integer> safeList = Collections.synchronizedList(list); // 包装为安全的 list

        new Thread(() -> {
            for (int i = 0; true; i++) {
                safeList.add(i); // 加锁
            }
        }, "t1").start();

        new Thread(() -> {
            synchronized (safeList) { // 这里必需加锁，因为 safeList 没有给 iterator 加锁
                Iterator<Integer> iterator = safeList.iterator();
                while (iterator.hasNext()) {
                    Integer next = iterator.next();
                    System.out.println("i = " + next);
                }
            }
        }, "t2").start();
    }

    public static void  safeCopyOnWriteList() {
        CopyOnWriteArrayList<Integer> safeList = new CopyOnWriteArrayList<>();
        new Thread(() -> {
            for (int i = 0; true; i++) {
                safeList.add(i); // 写时拷贝
            }
        }, "t1").start();

        new Thread(() -> {
            Iterator<Integer> iterator = safeList.iterator();
            while (iterator.hasNext()) { // 无需加锁，因为读取的是复本
                Integer next = iterator.next();
                System.out.println("i = " + next);
            }
        }, "t2").start();
    }
}
