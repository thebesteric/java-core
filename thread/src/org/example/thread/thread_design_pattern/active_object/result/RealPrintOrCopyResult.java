package org.example.thread.thread_design_pattern.active_object.result;

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
