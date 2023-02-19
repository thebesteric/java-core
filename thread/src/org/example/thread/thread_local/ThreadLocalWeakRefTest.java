package org.example.thread.thread_local;

import lombok.Getter;
import lombok.Setter;

import java.lang.ref.WeakReference;

public class ThreadLocalWeakRefTest {
    public static void main(String[] args) throws InterruptedException {
        // weakRefCanBeGC();
        weakRefCanNotBeGC();
    }

    /**
     * 不会被回收：有强引用
     */
    public static void weakRefCanNotBeGC() throws InterruptedException {
        // 变量 man 就是强引用
        Man man = new Man("Lisi");
        Person person = new Person(man);

        System.out.println("GC 之前：man = " + person.get());

        System.gc();
        Thread.sleep(3000);

        System.out.println("GC 之后：man = " + person.get());
    }

    /**
     * 会被回收
     */
    public static void weakRefCanBeGC() throws InterruptedException {
        Person person = new Person(new Man("ZhangSan"));

        System.out.println("GC 之前：man = " + person.get());

        System.gc();
        Thread.sleep(3000);

        System.out.println("GC 之后：man = " + person.get());
    }

    static class Person extends WeakReference<Man> {
        public Person(Man referent) {
            super(referent);
        }
    }

    @Getter
    @Setter
    static class Man {
        private String name;

        public Man(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return "This man's name is " + this.name;
        }

        @Override
        protected void finalize() throws Throwable {
            System.out.println("触发了 GC");;
        }
    }
}
