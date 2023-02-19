package org.example.thread.thread;

import com.sun.management.OperatingSystemMXBean;

import java.lang.management.ManagementFactory;

/**
 * <p>当虚拟机中，只要存在用户线程，虚拟机就不会结束
 * <p>当虚拟机中，用户线程都结束了，那么守护线程也会自动结束
 */
public class Damon {
    public static void main(String[] args) {
        System.out.println("main thread is " + Thread.currentThread().isDaemon());

        Thread daemonThread = new Thread(() -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("daemon thread is running " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread userThread = new Thread(() -> {
            for (int i = 1; i <= 3; i++) {
                System.out.println("user thread is running " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        daemonThread.setDaemon(true);
        userThread.setDaemon(false);

        daemonThread.start();
        userThread.start();

        // 主线程等待 5 秒
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static class DaemonThreadDemo {
        private static OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        public static void main(String[] args) {
            DaemonThreadDemo daemonThreadDemo = new DaemonThreadDemo();

            Thread monitorThread = new Thread(() -> {
                while (true) {
                    daemonThreadDemo.getCpuLoad();
                    daemonThreadDemo.getMemoryLoad();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "monitor-thread");
            monitorThread.setDaemon(true);
            monitorThread.start();

            new Thread(() -> {
                for (int i = 0; i < 10; i++) {
                    System.out.println("do business...");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, "business-thread").start();

        }

        public void getCpuLoad() {
            double cpuLoad = osmxb.getSystemCpuLoad();
            int percentCpuLoad = (int) (cpuLoad * 100);
            System.out.println("cpu load：" + percentCpuLoad);
        }

        public void getMemoryLoad() {
            double totalVirtualMemory = osmxb.getTotalPhysicalMemorySize();
            double freePhysicalMemorySize = osmxb.getFreePhysicalMemorySize();
            double value = freePhysicalMemorySize / totalVirtualMemory;
            int percentMemoryLoad = (int) ((1 - value) * 100);
            System.out.println("memory load：" + percentMemoryLoad);
        }
    }
}
