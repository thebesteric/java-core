package org.example.thread_design_pattern.balking;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Screen {

    private List<String> contents = new ArrayList<String>();

    private FileWriter fileWriter;

    // 电子屏是否发生变化
    private volatile boolean changed = false;

    private Screen(String docPath, String docName) throws IOException {
        this.fileWriter = new FileWriter(new File(docPath, docName));
    }

    public static Screen create(String docPath, String docName) throws IOException {
        Screen screen = new Screen(docPath, docName);
        new AutoFlushThread(screen).start();
        return screen;
    }

    public void save(boolean auto) throws IOException {
        synchronized (this) {
            // 如果没有更新，那么就不做
            if (!changed) {
                return;
            }

            // 发现有更改，需要刷新，并入库
            for (String content : contents) {
                String message = (auto ? "【自动刷新】" : "【手动刷新】") + content;
                this.fileWriter.write(message + "\r\n");
                System.out.println(message);
            }
            this.fileWriter.flush();
            changed = false;
        }
    }

    public void add(String name, int room) throws IOException {
        synchronized (this) {
            String content = name + " ==== " + room + " 号门诊";
            this.contents.add(content);
            this.changed = true;
        }

    }
}
