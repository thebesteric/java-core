package org.example.thread.thread_design_pattern.guarded_suspension;

public class GuardedSuspensionTest {
    public static void main(String[] args) {
        Cainiao cainiao = new Cainiao();
        new Postman("快递员", cainiao).start();
        new Person("张三", cainiao).start();
    }
}
