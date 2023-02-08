package org.example.thread_design_pattern.single_threaded_execution;

import lombok.Data;

import java.util.Random;

@Data
public class SafeGate {
    private int num;
    private String nickname;
    private String address;

    public synchronized void check(Customer customer) {
        num++;
        this.nickname = customer.getNickname();
        this.address = customer.getAddress();

        System.out.println("[" + num + "] " + customer.getNickname() + ": 进入安检");
        Random rand = new Random();
        try {
            Thread.sleep(rand.nextInt(3000));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (!nickname.equals(customer.getNickname())) {
            throw new IllegalArgumentException("[" + num + "] " + customer  + ": 非法闯入");
        }
        System.out.println("[" + num + "] " +customer.getNickname() + ": 检查通过");
    }

}
