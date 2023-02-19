package org.example.thread_design_pattern.active_object;

import org.example.thread_design_pattern.active_object.thread.CopyThread;
import org.example.thread_design_pattern.active_object.thread.PrintThread;

public class ActiveObjectTest {
    public static void main(String[] args) {

        // 获取主动对象
        ActiveObject proxy = ActiveObjectFactory.createActiveObject();

        new PrintThread("Hello World", 1, proxy).start();
        new PrintThread("Good Luck", 1, proxy).start();

        new CopyThread("明月几时有，把酒问青天", 1, proxy).start();
    }
}
