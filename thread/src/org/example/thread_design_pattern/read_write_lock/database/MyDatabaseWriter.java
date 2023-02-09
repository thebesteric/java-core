package org.example.thread_design_pattern.read_write_lock.database;

public class MyDatabaseWriter extends Thread {

    private final MyDatabase<String, Object> myDatabase;
    private String key;
    private Object value;

    public MyDatabaseWriter(MyDatabase<String, Object> myDatabase, String name) {
        super(name);
        this.myDatabase = myDatabase;
    }

    public void write(String key, Object value) {
        this.key = key;
        this.value = value;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                myDatabase.set(key, value);
                System.out.println(getName() + " write: " + key + " = " + value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
