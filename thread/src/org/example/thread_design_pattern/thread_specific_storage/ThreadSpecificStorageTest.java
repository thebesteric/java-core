package org.example.thread_design_pattern.thread_specific_storage;

public class ThreadSpecificStorageTest {
    public static void main(String[] args) {
        UserSpecificInfo userInfo = new UserSpecificInfo();
        for (int i = 1; i <= 100; i++) {
            int finalI = i;
            new Thread(()->{
                userInfo.setId(finalI);
                userInfo.setName("name"+ finalI);
                userInfo.setAddress("address" + finalI);
                int id = userInfo.getId();
                String name = userInfo.getName();
                String address = userInfo.getAddress();
                System.out.println("id = " + id + ", name = " + name + ", address = " + address);
            }).start();
        }

    }
}
