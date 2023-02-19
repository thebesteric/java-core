package org.example.thread.thread_design_pattern.future.spider;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;

public class ContentImpl implements Content {

    private final byte[] contentBytes;

    public ContentImpl(String urlStr) {
        try {
            URL url = new URL(urlStr);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());
            byte[] bytes = new byte[dataInputStream.available()];
            dataInputStream.readFully(bytes);
            contentBytes = new byte[bytes.length];
            System.arraycopy(bytes, 0, contentBytes, 0, contentBytes.length);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getContent() {
        if (contentBytes == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return contentBytes;
    }
}
