package org.example.thread_design_pattern.single_threaded_execution;

public class SingleThreadedExecutionTest {
    public static void main(String[] args) {
        SafeGate safeGate = new SafeGate();
        new Customer("张三", "北京").check(safeGate);
        new Customer("李四", "上海").check(safeGate);
        new Customer("王五", "合肥").check(safeGate);
    }
}
