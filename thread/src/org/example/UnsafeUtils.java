package org.example;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeUtils {

    public static final Unsafe unsafe = getUnsafe();

    public static Unsafe getUnsafe() {
        if (unsafe != null) return unsafe;
        try {
            Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void sleep(long millis) {
        long nano = millis * 1000 * 1000;
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (end < start + nano);
    }
}
