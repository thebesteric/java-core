package org.example.thread_design_pattern.future;

public class FutureProduct implements Product {
    private boolean ready = false;
    private RealProduct realProduct;
    @Override
    public synchronized String getContent() {
        while (!ready) {
            try {
                System.out.println(Thread.currentThread().getName() + " is waiting...");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return realProduct.getContent();
    }

    public synchronized void setContent(RealProduct realProduct) {
        this.realProduct = realProduct;
        this.ready = true;
        notifyAll();
    }
}
