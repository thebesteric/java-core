package org.example.juc;

import sun.misc.Unsafe;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class MyUnsafe {
    public static void main(String[] args) throws Exception {
        Unsafe unsafe = null;

        // 方式一：通过反射构造方法获取 Unsafe 类
        Constructor<?> constructor = Unsafe.class.getDeclaredConstructors()[0];
        constructor.setAccessible(true);
        unsafe = (Unsafe) constructor.newInstance();
        System.out.println(unsafe);

        // 方式二：通过反射字段获取 Unsafe 类
        Field field = Unsafe.class.getDeclaredField("theUnsafe");
        field.setAccessible(true);
        unsafe = (Unsafe) field.get(null);
        System.out.println(unsafe);
    }

    public static Unsafe getUnsafe() {
        try {
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return (Unsafe) field.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
