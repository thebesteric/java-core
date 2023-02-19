package org.example.thread.thread_pool.simulate;

import lombok.Getter;
import lombok.Setter;

import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

@Getter
@Setter
public class MyThreadPoolExecutor extends Thread {

    /**
     * 核心线程数
     */
    private int corePoolSize;
    /**
     * 最大线程数
     */
    private int maximumPoolSize;
    /**
     * 线程池是否关闭
     */
    private volatile boolean threadPoolClose = false;
    /**
     * 存放任务的队列
     */
    private static BlockingQueue<Runnable> taskQueue;
    /**
     * 存放线程的队列
     */
    private BlockingQueue<WorkerThread> threadQueue = new LinkedBlockingDeque<>();
    /**
     * 存放线程的队列
     */
    private BlockingQueue<WorkerThread> tempThreadQueue = new LinkedBlockingDeque<>();
    /**
     * 拒绝策略
     */
    private MyRejectedExecutionHandler rejectedExecutionHandler;

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize) {
        this(corePoolSize, maximumPoolSize, new LinkedBlockingDeque<>(128));
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> taskQueue) {
        this(corePoolSize, maximumPoolSize, taskQueue, new MyAbortPolicy());
    }

    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, BlockingQueue<Runnable> taskQueue, MyRejectedExecutionHandler rejectedExecutionHandler) {
        this.corePoolSize = corePoolSize;
        this.maximumPoolSize = maximumPoolSize;
        this.taskQueue = taskQueue;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
        init();
    }

    public void init() {
        for (int i = 0; i < corePoolSize; i++) {
            addWorker(false);
        }
        this.start();
    }

    private void addWorker(boolean temp) {
        WorkerThread workerThread = new WorkerThread(temp);
        workerThread.start();
        if (!temp) {
            threadQueue.add(workerThread);
        } else {
            tempThreadQueue.add(workerThread);
        }
    }

    private void removeWorker(boolean temp) {
        Iterator<WorkerThread> iterator;
        if (!temp) {
            iterator = threadQueue.iterator();
        } else {
            iterator = tempThreadQueue.iterator();
        }
        while (iterator.hasNext()) {
            WorkerThread workerThread = iterator.next();
            if (WorkerThreadState.BLOCK == workerThread.getStates()) {
                workerThread.close();
                threadQueue.notifyAll();
                threadQueue.remove();
                break;
            }
        }
    }

    public void execute(Runnable task) {
        if (threadPoolClose) {
            throw new RuntimeException("线程池已经关闭，无法提交任务");
        }


        synchronized (taskQueue) {
            try {
                if (taskQueue.remainingCapacity() == 0 && corePoolSize + tempThreadQueue.size() >= maximumPoolSize) {
                    rejectedExecutionHandler.rejectedExecution(task, this);
                }
                // if (taskQueue.remainingCapacity() == 0 && tempThreadQueue.size() < maximumPoolSize - corePoolSize) {
                //     addWorker(true);
                // }


                // 优先核心线程先执行
                // for (WorkerThread coreThread : threadQueue) {
                //     if (WorkerThreadState.BLOCK == coreThread.getStates()) {
                //         coreThread.setRunnableTask(task);
                //         taskQueue.notify();
                //         return;
                //     }
                // }

                taskQueue.add(task);
                taskQueue.notifyAll();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        // 先检查任务队列
        while (!taskQueue.isEmpty()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        // 检查线程状态
        synchronized (threadQueue) {
            while (threadQueue.size() > 0) {
                WorkerThread workerThread = threadQueue.peek();
                if (WorkerThreadState.BLOCK == workerThread.getStates() || WorkerThreadState.DEAD == workerThread.getStates()) {
                    workerThread.close();
                    threadQueue.notifyAll();
                    threadQueue.remove();
                }
            }
        }
        threadPoolClose = true;
        System.out.println("线程池已关闭");
    }

    int min = 3;
    int active = 5;
    int max = 10;

    @Override
    public void run() {
        while(!threadPoolClose) {
            try {
                Thread.sleep(1000);
                System.out.println("======================================");
                System.out.println("最小线程数：" + min + ", 活动线程数：" + active + ", 最大线程数：" + max);
                System.out.println("当前线程数：" + threadQueue.size());
                System.out.println("当前任务数：" + taskQueue.size());
                System.out.println("======================================");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            synchronized (threadQueue) {
                // === 扩容 ===
                // active < 任务数 < max 且 当前线程数 < active
                if(taskQueue.size() > active && threadQueue.size() < active) {
                    for(int i = threadQueue.size(); i < active ; i++) {
                        addWorker(false);
                    }
                    System.out.println("====> 线程数已经扩容到 active：" + threadQueue.size());
                }
                // 任务数 > max 且 当前线程数 < max
                else if(taskQueue.size() > max && threadQueue.size() < max) {
                    for(int i = threadQueue.size(); i < max; i++) {
                        addWorker(false);
                    }
                    System.out.println("====> 线程数已经扩容到 max：" + threadQueue.size());
                }

                // === 缩容 ===
                if (taskQueue.isEmpty() && threadQueue.size() > active) {
                    while(threadQueue.size() > active) {
                        removeWorker(false);
                    }
                    System.out.println("====> 线程数已经缩容到 active：" + threadQueue.size());
                }
            }
        }
    }

    enum WorkerThreadState {
        FREE, BLOCK, RUNNING, DEAD;
    }


    static class WorkerThread extends Thread {
        @Getter
        private WorkerThreadState states = WorkerThreadState.FREE;

        private boolean temp = false;

        @Setter
        private Runnable runnableTask;

        public WorkerThread(boolean temp) {
            this.temp = temp;
        }

        @Override
        public void run() {
            while (WorkerThreadState.DEAD != states) {
                Runnable task = runnableTask;
                synchronized (taskQueue) {
                    while (taskQueue.isEmpty() && task == null) {
                        try {
                            states = WorkerThreadState.BLOCK;
                            taskQueue.wait();
                            task = runnableTask;
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    // 要不然就是有 runnableTask，要不就是 taskQueue 有任务
                    if (task == null) {
                        task = taskQueue.remove();
                    }
                }

                if (task != null) {
                    states = WorkerThreadState.RUNNING;
                    task.run();
                    runnableTask = null;
                    states = WorkerThreadState.FREE;
                }
            }
        }

        public void close() {
            this.states = WorkerThreadState.DEAD;
        }
    }

    static class MyTask implements Runnable {
        @Getter
        @Setter
        private int no;

        public MyTask(int no) {
            this.no = no;
        }

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " start: " + no + " 号任务");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(Thread.currentThread().getName() + " end");
        }

        @Override
        public String toString() {
            return "【MyTask: " + no + "】";
        }
    }

}
