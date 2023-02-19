package org.example.thread.thread_design_pattern.two_phase_terminate;

public class Worker extends Thread {

    private int progress = 0;
    private volatile boolean stop = false;

    public Worker(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println(getName() + ": 开始工作...");

        try {
            // 第一阶段停止：判断 stop 状态
            while (!stop) {
                System.out.println(getName() + ": 正在上班 " + (++progress) + "%");
                if (progress == 100) {
                    System.out.println(getName() + ": 工作结束...");
                    break;
                }
                Thread.sleep(100);
            }
        } catch (InterruptedException e) { // 第二阶段中止：发起中断，防止线程中执行了 wait 或长时间的 sleep
            e.printStackTrace();
        } finally {
            if (stop) {
                System.out.println(getName() + ": 向老板请假回家");
                System.out.println(getName() + ": 今天的工作完成了 " + progress + "%, 明天继续...");
            }
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop; // 第一阶段中止：修改中止标识符
        interrupt(); // 第二阶段中止：发起中断，防止线程中执行了 wait 或长时间的 sleep
    }
}
