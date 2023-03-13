package org.example.juc.atomic;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.atomic.AtomicReference;

public class AtomicReferenceTest {

    static volatile Member member = new Member(1, "eric", 0);
    static AtomicReference<Member> atomicReferenceMember = new AtomicReference<>(new Member(1, "eric", 0));

    public static void main(String[] args) throws InterruptedException {
        Thread[] threads = new Thread[20];
        for (int i = 0; i < 20; i++) {
            threads[i] = new Thread(() -> {
                // Member memberTemp = member;
                // Member memberNew = new Member(memberTemp.getId(), memberTemp.getName(), memberTemp.getPoint() + 50);
                // member = memberNew;
                // System.out.println(memberNew);

                while (true) {
                    Member memberTemp = atomicReferenceMember.get();
                    Member memberNew = new Member(memberTemp.getId(), memberTemp.getName(), memberTemp.getPoint() + 50);
                    if (atomicReferenceMember.compareAndSet(memberTemp, memberNew)) {
                        System.out.println(Thread.currentThread().getName() + " = " + memberNew);
                        break;
                    }
                }

            }, "t" + i);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // System.out.println("member = " + member);
        System.out.println("member = " + atomicReferenceMember.get());
    }


    @Data
    @AllArgsConstructor
    static class Member {
        private int id;
        private String name;
        private int point;
    }
}
