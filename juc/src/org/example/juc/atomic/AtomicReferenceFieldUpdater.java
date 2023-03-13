package org.example.juc.atomic;

import org.example.juc.MyUnsafe;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class AtomicReferenceFieldUpdater<T> {
    private static final Unsafe UNSAFE = MyUnsafe.getUnsafe();

    private final long offset;

    public AtomicReferenceFieldUpdater(final Class<T> tClass, final String fieldName) {
        Field field = null;
        try {
            field = tClass.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);
        this.offset = UNSAFE.objectFieldOffset(field);
    }

    public int addAndGet(T obj, int delta) {
        return getAndAdd(obj, delta) + delta;
    }

    public int getAndAdd(T obj, int delta) {
        return UNSAFE.getAndAddInt(obj, offset, delta);
    }

}
