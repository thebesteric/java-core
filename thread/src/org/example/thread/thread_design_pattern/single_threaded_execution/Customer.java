package org.example.thread.thread_design_pattern.single_threaded_execution;

import lombok.Data;

@Data
public class Customer {

    private String nickname;
    private String address;

    public Customer(String nickname, String address) {
        this.nickname = nickname;
        this.address = address;
    }

    public void check(SafeGate safeGate) {
        new Thread(() -> {
            safeGate.check(this);
        }).start();
    }
}
