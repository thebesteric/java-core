package org.example.juc;

import lombok.Data;
import sun.misc.Unsafe;

public class UnsafeOperateObject {
    public static void main(String[] args) throws Exception {
        Unsafe unsafe = MyUnsafe.getUnsafe();

        // 构造方法不会执行，静态代码快会执行
        Man man = (Man) unsafe.allocateInstance(Man.class);
        System.out.println("man = " + man);

        Home home = new Home();
        home.setNo(888);
        home.setMan(new Man("eric-01", 18));

        // 获取 no 的偏移量
        long noOffset = unsafe.objectFieldOffset(Home.class.getDeclaredField("no"));
        // 获取对象 man 的偏移量
        long manOffset = unsafe.objectFieldOffset(Home.class.getDeclaredField("man"));
        // 获取静态字段 COUNTRY 的偏移量
        long staticCountryOffset = unsafe.staticFieldOffset(Home.class.getDeclaredField("COUNTRY"));

        // 通过对象和偏移量获取 no 的值
        System.out.println("get no = " + unsafe.getInt(home, noOffset));
        // 通过对象和偏移量获取 man 的值
        System.out.println("get man = " + unsafe.getObject(home, manOffset));
        // 通过 class 对象和偏移量获取 COUNTRY 的值
        System.out.println("get COUNTRY = " + unsafe.getObject(Home.class, staticCountryOffset)); // 静态字段是和 class 字节码对象的

        // 通过对象和偏移量设置 no 的值
        unsafe.putInt(home, noOffset, 999);
        // 通过对象和偏移量设置 man 的值
        unsafe.putObject(home, manOffset, new Man("eric-02", 20));
        // 通过 class 对象和偏移量设置 COUNTRY 的值
        unsafe.putObject(Home.class, staticCountryOffset, "CHINA");

        System.out.println("after put, get no = " + unsafe.getInt(home, noOffset));
        System.out.println("after put, get man = " + unsafe.getObject(home, manOffset));
        System.out.println("after put, get COUNTRY = " + unsafe.getObject(Home.class, staticCountryOffset));

        long addressOffset = unsafe.objectFieldOffset(Home.class.getDeclaredField("address"));
        unsafe.putObjectVolatile(home, addressOffset, "Anhui HeFei"); // 会有 lock 指令前缀
        // unsafe.putOrderedObject(home, addressOffset, "Anhui HeFei"); // 不会添加 lock 指令前缀
        System.out.println("address = " + unsafe.getObject(home, addressOffset));
    }

    @Data
    static class Home {
        private int no;
        private Man man;
        private static final String COUNTRY = "AMERICA";
        private String address;
    }

    @Data
    static class Man {
        private String name;
        private int age;

        public Man() {
            System.out.println("non-args constructor running...");
        }

        public Man(String name, int age) {
            System.out.println("full-args constructor running...");
            this.name = name;
            this.age = age;
        }

        static {
            System.out.println("static block running...");
        }
    }
}
