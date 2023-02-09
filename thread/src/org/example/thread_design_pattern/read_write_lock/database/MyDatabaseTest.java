package org.example.thread_design_pattern.read_write_lock.database;

public class MyDatabaseTest {
    public static void main(String[] args) throws InterruptedException {
        MyDatabase<String, Object> myDatabase = new MyDatabase<>();

        new MyDatabaseWriter(myDatabase, "w1").write("zhangsan", 18);
        new MyDatabaseWriter(myDatabase, "w2").write("lisi", 19);
        new MyDatabaseWriter(myDatabase, "w3").write("wangwu", 20);

        new MyDatabaseReader(myDatabase, "r1").read("zhangsan");
        new MyDatabaseReader(myDatabase, "r2").read("lisi");
        new MyDatabaseReader(myDatabase, "r3").read("wangwu");
        new MyDatabaseReader(myDatabase, "r4").read("zhangsan");
        new MyDatabaseReader(myDatabase, "r5").read("lisi");
        new MyDatabaseReader(myDatabase, "r6").read("wangwu");
        new MyDatabaseReader(myDatabase, "r7").read("zhangsan");
        new MyDatabaseReader(myDatabase, "r8").read("lisi");
        new MyDatabaseReader(myDatabase, "r9").read("wangwu");
    }
}
