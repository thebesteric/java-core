package org.example.juc.atomic;

import lombok.Data;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 掩饰 AtomicReference 是否能确保对象内部属性的安全性
 */
public class AtomicReferenceInnerAttrTest {

    private static AtomicReference<Account> atomicReference = new AtomicReference<>();

    public static void main(String[] args) throws InterruptedException {
        Account account = new Account(0);
        atomicReference.set(account);

        CountDownLatch countDownLatch = new CountDownLatch(100);
        for (int i = 0; i < 100; i++) {
            new Thread(()->{
                // add(1);
                atomicAdd(1);
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();

        System.out.println(atomicReference.get().getMoney()); // 97
    }

    private static void add(int money) {
        Account account = atomicReference.get();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        account.setMoney(account.getMoney() + money); // 修改了引用对象内部属性，无法保证
    }

    private static void atomicAdd(int money) {
        Account account = atomicReference.get();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        AtomicReferenceFieldUpdater<Account> updater = new AtomicReferenceFieldUpdater<>(Account.class, "money");
        updater.addAndGet(account, money);
    }

    @Data
    static class Account {
        private int money;
        // private AtomicInteger money;

        public Account(int money) {
            this.money = money;
        }
    }
}
