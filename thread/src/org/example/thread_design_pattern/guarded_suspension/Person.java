package org.example.thread_design_pattern.guarded_suspension;

public class Person extends Thread {
    private String name;
    private Cainiao cainiao;

    public Person(String name, Cainiao cainiao){
        super(name);
        this.name = name;
        this.cainiao = cainiao;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            Package pack = cainiao.get();
            System.out.println(Thread.currentThread().getName() + ": 取快递 " + pack.getName());
        }
    }
}
