package org.example.thread.thread_design_pattern.active_object;

import org.example.thread.thread_design_pattern.active_object.result.RealPrintOrCopyResult;

public class PrintOrCopyService implements ActiveObject {
    @Override
    public RealPrintOrCopyResult printContent(String content) {
        System.out.println("打印: " + content);
        return new RealPrintOrCopyResult(content);
    }

    @Override
    public void copyContent(String content) {
        System.out.println("复印: " + content);
    }
}
