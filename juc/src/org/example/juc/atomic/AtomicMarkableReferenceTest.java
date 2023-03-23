package org.example.juc.atomic;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class AtomicMarkableReferenceTest {
    public static void main(String[] args) throws InterruptedException {
        AtomicMarkableReference<String> atomicMarkableReference = new AtomicMarkableReference<>("张三", false);
        boolean marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
        String reference = atomicMarkableReference.getReference();
        System.out.println("reference = " + reference);

        boolean ret = atomicMarkableReference.compareAndSet(reference, "lisi", false, true);
        if (ret) {
            marked = atomicMarkableReference.isMarked();
            System.out.println("marked = " + marked);
            reference = atomicMarkableReference.getReference();
            System.out.println("reference = " + reference);
        }

        atomicMarkableReference.set("A", false);

        Thread t1 = new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            boolean succeed = atomicMarkableReference.compareAndSet("A", "C", false, true);
            if (succeed) {
                System.out.println("t1 修改 A = C");
            }
        });

        Thread t2 = new Thread(() -> {
            boolean succeed = atomicMarkableReference.compareAndSet("A", "B", false, true);
            if (succeed) {
                System.out.println("t2 修改 A = B");
            }
            succeed = atomicMarkableReference.compareAndSet("B", "A", true, false);
            if (succeed) {
                System.out.println("t2 修改 B = A");
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        marked = atomicMarkableReference.isMarked();
        System.out.println("marked = " + marked);
        reference = atomicMarkableReference.getReference();
        System.out.println("reference = " + reference);

    }
}
