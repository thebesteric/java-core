package org.example.thread_design_pattern.active_object.request;

import org.example.thread_design_pattern.active_object.result.FuturePrintOrCopyResult;
import org.example.thread_design_pattern.active_object.PrintOrCopyService;

public abstract class PrintOrCopyRequest {
    protected PrintOrCopyService printOrCopyService;
    protected FuturePrintOrCopyResult futurePrintOrCopyResult;

    protected PrintOrCopyRequest(PrintOrCopyService printOrCopyService, FuturePrintOrCopyResult futurePrintOrCopyResult) {
        this.printOrCopyService = printOrCopyService;
        this.futurePrintOrCopyResult = futurePrintOrCopyResult;
    }

    public abstract void execute();
}
