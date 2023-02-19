package org.example.thread.thread_design_pattern.active_object.juc;

import org.example.thread.thread_design_pattern.active_object.juc.thread.CopyThread;
import org.example.thread.thread_design_pattern.active_object.juc.thread.PrintThread;

public class ActiveObjectTest {
    public static void main(String[] args) {

        // 获取主动对象
        ActiveObject proxy = ActiveObjectFactory.createActiveObject();

        new PrintThread("Hello World", 1, proxy).start();
        new PrintThread("Good Luck", 3, proxy).start();

        new CopyThread("明月几时有，把酒问青天", 1, proxy).start();
    }
}
