package org.example.thread_design_pattern.active_object;

import org.example.thread_design_pattern.active_object.result.PrintOrCopyResult;

public interface ActiveObject {
    // 打印
    PrintOrCopyResult printContent(String content);

    // 复印
    void copyContent(String content);
}
