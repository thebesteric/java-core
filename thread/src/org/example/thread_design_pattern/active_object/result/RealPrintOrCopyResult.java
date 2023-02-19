package org.example.thread_design_pattern.active_object.result;

import org.example.thread_design_pattern.active_object.result.PrintOrCopyResult;

public class RealPrintOrCopyResult implements PrintOrCopyResult {

    private final String result;

    public RealPrintOrCopyResult(String result) {
        this.result = result;
    }

    @Override
    public String getResult() {
        return result;
    }
}
