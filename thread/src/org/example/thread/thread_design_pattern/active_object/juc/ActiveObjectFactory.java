package org.example.thread.thread_design_pattern.active_object.juc;

public class ActiveObjectFactory {

    public static ActiveObject createActiveObject() {
        return new PrintOrCopyProxy();
    }
}
