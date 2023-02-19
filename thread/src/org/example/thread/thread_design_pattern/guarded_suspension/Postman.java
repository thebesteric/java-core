package org.example.thread.thread_design_pattern.guarded_suspension;

public class Postman extends Thread {
    private String name;
    private Cainiao cainiao;

    public Postman(String name, Cainiao cainiao){
        super(name);
        this.name = name;
        this.cainiao = cainiao;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            String packageName = "package-"+i;
            System.out.println(Thread.currentThread().getName() + ": 投送快递 " + packageName);
            cainiao.add(new Package(packageName));
        }
    }
}
