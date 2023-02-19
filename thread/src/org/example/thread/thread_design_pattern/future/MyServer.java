package org.example.thread.thread_design_pattern.future;

public class MyServer {

    public FutureProduct request(int count, char c) {
        System.out.println(Thread.currentThread().getName() + ": request count -> " + count + " char -> " + c + " starting...");
        // 创建"提货单"
        FutureProduct futureProduct = new FutureProduct();

        // 生产数据
        new Thread(() -> {
            RealProduct realProduct = new RealProduct(count, c);
            futureProduct.setContent(realProduct);
        }).start();

        System.out.println(Thread.currentThread().getName() + ": request count -> " + count + " char -> " + c + " end");
        // 返回"提货单"
        return futureProduct;
    }

}
