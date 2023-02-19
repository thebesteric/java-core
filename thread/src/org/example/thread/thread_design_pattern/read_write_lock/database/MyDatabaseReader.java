package org.example.thread.thread_design_pattern.read_write_lock.database;

public class MyDatabaseReader extends Thread {

    private final MyDatabase<String, Object> myDatabase;

    private String key;

    public MyDatabaseReader(MyDatabase<String, Object> myDatabase, String name) {
        super(name);
        this.myDatabase = myDatabase;
    }

    public void read(String key) {
        this.key = key;
        this.start();
    }
    @Override
    public void run() {
        while(true) {
            try {
                Object value = myDatabase.get(key);
                System.out.println(getName() + " read: " + key + " " + value);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
