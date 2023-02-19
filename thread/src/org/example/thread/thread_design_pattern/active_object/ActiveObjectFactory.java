package org.example.thread.thread_design_pattern.active_object;

import org.example.thread.thread_design_pattern.active_object.thread.ExecutorThread;

public class ActiveObjectFactory {

    public static ActiveObject createActiveObject() {
        PrintOrCopyService service = new PrintOrCopyService();
        RequestQueue requestQueue = new RequestQueue(10);

        ExecutorThread executor = new ExecutorThread(requestQueue);
        executor.start();

        PrintOrCopyProxy proxy = new PrintOrCopyProxy(service, requestQueue);
        return proxy;
    }
}
