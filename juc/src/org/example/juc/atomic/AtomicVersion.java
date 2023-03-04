package org.example.juc.atomic;

import lombok.Getter;
import lombok.Setter;
import org.example.juc.MyUnsafe;
import sun.misc.Unsafe;

public class AtomicVersion<V> implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    /*
     * This class intended to be implemented using VarHandles, but there
     * are unresolved cyclic startup dependencies.
     */
    // private static final jdk.internal.misc.Unsafe U = jdk.internal.misc.Unsafe.getUnsafe();
    private static final Unsafe U = MyUnsafe.getUnsafe();

    private static final long VALUE_OFFSET;
    private static final long VALUE_AND_VERSION_OFFSET;

    static {
        try {
            VALUE_OFFSET = U.objectFieldOffset(ValueAndVersion.class.getDeclaredField("value"));
            VALUE_AND_VERSION_OFFSET = U.objectFieldOffset(AtomicVersion.class.getDeclaredField("valueAndVersion"));
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Setter
    private static class ValueAndVersion<T> {
        private volatile T value;
        private volatile int version;

        public ValueAndVersion(T value, int version) {
            this.value = value;
            this.version = version;
        }
    }
    private volatile ValueAndVersion<V> valueAndVersion;

    public AtomicVersion(V initialValue, int initialVersion) {
        this.valueAndVersion = new ValueAndVersion<>(initialValue, initialVersion);

    }

    public AtomicVersion(V initialValue) {
        this.valueAndVersion = new ValueAndVersion<>(initialValue, 0);
    }

    public final V getValue() {
        return valueAndVersion.getValue();
    }

    public final int getVersion() {
        return valueAndVersion.getVersion();
    }

    public final ValueAndVersion<V> get() {
        return valueAndVersion;
    }

    public final void set(V newValue, int newVersion) {
        if (newValue != valueAndVersion.getValue() || newVersion != valueAndVersion.getVersion()) {
            valueAndVersion = new ValueAndVersion<>(newValue, newVersion);
        }
    }

    public final void set(V newValue) {
        this.valueAndVersion.value = newValue;
    }

    @SuppressWarnings("unchecked")
    public final ValueAndVersion<V> getAndSet(V newValue, int newVersion) {
        return (ValueAndVersion<V>) U.getAndSetObject(this, VALUE_AND_VERSION_OFFSET, new ValueAndVersion<>(newValue, newVersion));
    }

    public final boolean compareAndSet(V expectedValue, V newValue) {
        return U.compareAndSwapObject(this.valueAndVersion, VALUE_OFFSET, expectedValue, newValue);
    }

    public final boolean compareAndSet(V expectedValue, int expectedVersion, V newValue, int newVersion) {
        ValueAndVersion<V> currValueAndVersion = this.valueAndVersion;
        return expectedValue == currValueAndVersion.value && expectedVersion == currValueAndVersion.version
                && ((newValue == currValueAndVersion.value && newVersion == currValueAndVersion.version)
                || U.compareAndSwapObject(this, VALUE_AND_VERSION_OFFSET, currValueAndVersion, new ValueAndVersion<>(newValue, newVersion)));
    }

    public final int incrementAndGet() {
        return U.getAndAddInt(this.valueAndVersion, VALUE_OFFSET, 1) + 1;
    }

}
