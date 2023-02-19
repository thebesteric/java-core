package org.example.thread_design_pattern.active_object;


import org.example.thread_design_pattern.active_object.request.CopyRequest;
import org.example.thread_design_pattern.active_object.request.PrintRequest;
import org.example.thread_design_pattern.active_object.result.FuturePrintOrCopyResult;
import org.example.thread_design_pattern.active_object.result.PrintOrCopyResult;

public class PrintOrCopyProxy implements ActiveObject {

    private final PrintOrCopyService printOrCopyService;
    private final RequestQueue requestQueue;

    public PrintOrCopyProxy(PrintOrCopyService printOrCopyService, RequestQueue requestQueue) {
        this.printOrCopyService = printOrCopyService;
        this.requestQueue = requestQueue;
    }

    @Override
    public PrintOrCopyResult printContent(String content) {
        FuturePrintOrCopyResult futurePrintOrCopyResult = new FuturePrintOrCopyResult();
        PrintRequest printRequest = new PrintRequest(content, printOrCopyService, futurePrintOrCopyResult);
        // 入队
        try {
            requestQueue.put(printRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return futurePrintOrCopyResult;
    }

    @Override
    public void copyContent(String content) {
        CopyRequest copyRequest = new CopyRequest(content, printOrCopyService);
        // 入队
        try {
            requestQueue.put(copyRequest);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
