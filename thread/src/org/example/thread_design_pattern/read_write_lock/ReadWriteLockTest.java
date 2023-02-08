package org.example.thread_design_pattern.read_write_lock;

public class ReadWriteLockTest {
    public static void main(String[] args) {
        TextData textData = new TextData(7);
        new ReaderThread(textData, "r1").start();
        new ReaderThread(textData, "r2").start();
        new WriterThread(textData, "w1", "ABCDEFG").start();
        new WriterThread(textData, "w1", "1234567").start();
    }
}
