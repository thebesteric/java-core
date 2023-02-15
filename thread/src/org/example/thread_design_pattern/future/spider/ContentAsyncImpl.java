package org.example.thread_design_pattern.future.spider;

public class ContentAsyncImpl implements Content {

    private byte[] contentBytes;
    private boolean ready = false;

    @Override
    public synchronized byte[] getContent() {
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return contentBytes;
    }

    public synchronized void setContent(byte[] contentBytes) {
        this.contentBytes = contentBytes;
        this.ready = true;
        notifyAll();
    }
}
