package org.example.thread.thread_design_pattern.active_object.juc;

import java.util.concurrent.Future;

public interface ActiveObject {
    // 打印
    Future<String> printContent(String content);

    // 复印
    void copyContent(String content);

    void shutdown();
}
