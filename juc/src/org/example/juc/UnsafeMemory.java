package org.example.juc;

import sun.misc.Unsafe;

import java.util.Arrays;

public class UnsafeMemory {

    static Unsafe unsafe = MyUnsafe.getUnsafe();

    public static void main(String[] args) {
        long address = allocateMemory(8);
        setMemory(address, 8, (byte) 1); // 00000001 00000001 00000001 00000001 ... 00000001，共 8 个
        System.out.println(unsafe.getByte(address)); // 获取一个字节的值：00000001 = 1
        System.out.println(unsafe.getInt(address)); // 获取 4 个字节的值：00000001 00000001 00000001 00000001 = 16843009
        System.out.println(unsafe.getLong(address)); // 获取 8 个字节的值：00000001 00000001 00000001 00000001 ... 00000001 = 72340172838076673

        writeAddress(address, 2023);
        readAddress(address);
        long destAddress = allocateMemory(8);
        copyMemory(address, destAddress, 8);
        readAddress(destAddress);

        long newAddress = reallocateMemory(address, 16);
        readAddress(newAddress);

        // setMemory 案例：将数组 [1, 2, 3, 4] 设置为 [1, 2, 50529027, 4]
        int[] arr1 = {1, 2, 3, 4};
        int arrayBaseOffset = unsafe.arrayBaseOffset(int[].class); // 获取数组开始位置的指针
        setMemory(arr1, arrayBaseOffset + 8, 4, (byte) 3); // 00000011 00000011 00000011 00000011 00000011 00000011 00000011 00000011
        System.out.println("arr1 = " + Arrays.toString(arr1)); // [1, 2, 50529027, 4]

        // copyMemory 案例：将数组 arr2 [1, 2, 3, 4] 拷贝为 arr3 的 [1, 2, 3, 4]
        int[] arr2 = {1, 2, 3, 4};
        int[] arr3 = new int[arr1.length];
        arrayBaseOffset = unsafe.arrayBaseOffset(int[].class); // 获取数组开始位置的指针
        copyMemory(arr2, arrayBaseOffset, arr3, arrayBaseOffset, 16);
        System.out.println("arr3 = " + Arrays.toString(arr3));


        freeMemory(address, destAddress);
    }

    /**
     * 分配堆外内存：分配的时堆外内存，不受 JVM 管理
     *
     * @param bytes 开辟的空间大小
     */
    static long allocateMemory(long bytes) {
        long address = unsafe.allocateMemory(bytes); // 分配的时堆外内存，不受 JVM 管理
        System.out.println("分配的 " + bytes + " bytes 的堆外内存 address: [" + address + "]");
        return address;
    }

    /**
     * 重新分配内存空间
     *
     * @param address 原地址
     * @param bytes   空间大小
     * @return 新地址
     */
    static long reallocateMemory(long address, int bytes) {
        long newAddress = unsafe.reallocateMemory(address, bytes);
        System.out.println("重新分配的 " + bytes + " bytes 的堆外内存 address: [" + newAddress + "]");
        return newAddress;
    }

    /**
     * 写地址：从地址中写入数据
     *
     * @param address 地址
     * @param num     数据
     */
    static void writeAddress(long address, long num) {
        unsafe.putLong(address, num);
        System.out.println("写入 address: [" + address + "] 中的值：" + num);
    }

    /**
     * 读地址：从地址中读取数据
     *
     * @param address 地址
     */
    static long readAddress(long address) {
        long num = unsafe.getLong(address);
        System.out.println("获取 address: [" + address + "] 中的值: " + num);
        return num;
    }

    /**
     * 内存拷贝
     *
     * @param srcAddress  原地址
     * @param destAddress 目标地址
     * @param bytes       拷贝的字节数
     */
    static void copyMemory(long srcAddress, long destAddress, long bytes) {
        unsafe.copyMemory(srcAddress, destAddress, bytes);
        System.out.println("拷贝 srcAddress: [" + srcAddress + "] 至 destAddress: [" + destAddress + "], 拷贝 " + bytes + " 个字节");
    }

    /**
     * 内存拷贝
     *
     * @param base       原地址
     * @param srcOffset  原地址偏移量
     * @param destBase   目标地址
     * @param destOffset 目标地址偏移量
     * @param bytes      拷贝的字节数
     */
    static void copyMemory(Object base, long srcOffset, Object destBase, long destOffset, long bytes) {
        unsafe.copyMemory(base, srcOffset, destBase, destOffset, bytes);
        System.out.println("拷贝 base: " + base + ", srcOffset: [" + srcOffset + "] 至 destBase: " + destBase + ", destAddress: [" + destOffset + "], 拷贝 " + bytes + " 个字节");
    }

    /**
     * 设置内存的值
     * 将给定内存块中的所有字节（bytes）设置为固定值（value）
     *
     * @param address 绝对地址
     * @param bytes   向后填充的字节数
     * @param value   填充值（通常用零填充）
     */
    static void setMemory(long address, long bytes, byte value) {
        unsafe.setMemory(address, bytes, value);
        System.out.println("初始化 address: [" + address + "] 中 " + bytes + " 个字节的值, 均为: " + value);
    }

    /**
     * 设置内存的值
     * 将给定内存块中的所有字节（bytes）设置为固定值（value）
     *
     * @param obj     对象
     * @param address 相对对象的地址
     * @param bytes   向后填充的字节数
     * @param value   填充值（通常用零填充）
     */
    static void setMemory(Object obj, long address, long bytes, byte value) {
        unsafe.setMemory(obj, address, bytes, value);
        System.out.println("初始化 address: [" + address + "] 中 " + bytes + " 个字节的值, 均为: " + value);
    }

    /**
     * 释放内存
     *
     * @param addresses 内存地址
     */
    static void freeMemory(long... addresses) {
        for (long address : addresses) {
            unsafe.freeMemory(address);
            System.out.println("释放了 [" + address + "] 内存空间");
        }
    }


}
