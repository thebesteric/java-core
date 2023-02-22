package org.example.juc;

import sun.misc.Unsafe;

public class UnsafeOperateArray {
    public static void main(String[] args) {
        Unsafe unsafe = MyUnsafe.getUnsafe();

        String[] arr = {"a", "b", "c", "d"};
        int arrayBaseOffset = unsafe.arrayBaseOffset(String[].class); // 获取数组实例数据的相对对象地址的偏移量
        System.out.println("arrayBaseOffset = " + arrayBaseOffset); // arrayBaseOffset = 16，表示 String[] 的偏移量是 16 个字节
        int offset = unsafe.arrayIndexScale(String[].class); // 获取 String[] 中每个元素的偏移量
        System.out.println("offset = " + offset); // offset = 4，表示 String[]，每个元素的间隔是 4 个字节

        for (int i = 0; i < arr.length; i++) {
            Object str = unsafe.getObject(arr, arrayBaseOffset + (long) offset * i);
            System.out.println("str = " + str);
        }
    }
}
