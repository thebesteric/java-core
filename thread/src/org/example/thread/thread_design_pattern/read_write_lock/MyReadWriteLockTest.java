package org.example.thread.thread_design_pattern.read_write_lock;

public class MyReadWriteLockTest {
    public static void main(String[] args) {
        TextData textData = new TextData(7);
        new ReaderThread(textData, "r1").start();
        new ReaderThread(textData, "r2").start();
        new ReaderThread(textData, "r3").start();
        new ReaderThread(textData, "r4").start();
        new ReaderThread(textData, "r5").start();
        new ReaderThread(textData, "r6").start();
        new WriterThread(textData, "w1", "ABCDEFG").start();
        new WriterThread(textData, "w2", "1234567").start();
    }
}
