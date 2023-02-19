package org.example.thread.thread_design_pattern.balking;

import java.io.IOException;

public class BalkingTest {
    public static void main(String[] args) throws IOException {
        String docPath = "/Users/weijwang/work/IdeaProjects/java-core/thread/src/org/example/thread_design_pattern/balking";
        String docName = "screen.txt";
        Screen screen = Screen.create(docPath, docName);
        DoctorThread doctorThread = new DoctorThread(screen);
        doctorThread.start();
    }
}
