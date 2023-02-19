package org.example.thread_design_pattern.active_object.result;

public class FuturePrintOrCopyResult implements PrintOrCopyResult {

    private RealPrintOrCopyResult result;
    private boolean ready = false;

    @Override
    public synchronized String getResult() {
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result.getResult();
    }

    public synchronized void setPrintResult(RealPrintOrCopyResult result) {
        this.result = result;
        this.ready = true;
        notifyAll();
    }
}
