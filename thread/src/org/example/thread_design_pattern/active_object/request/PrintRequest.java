package org.example.thread_design_pattern.active_object.request;

import org.example.thread_design_pattern.active_object.result.FuturePrintOrCopyResult;
import org.example.thread_design_pattern.active_object.PrintOrCopyService;
import org.example.thread_design_pattern.active_object.result.RealPrintOrCopyResult;

public class PrintRequest extends PrintOrCopyRequest {

    private final String content;

    public PrintRequest(String content, PrintOrCopyService printOrCopyService, FuturePrintOrCopyResult futurePrintOrCopyResult) {
        super(printOrCopyService, futurePrintOrCopyResult);
        this.content = content;
    }

    @Override
    public void execute() {
        RealPrintOrCopyResult realPrintOrCopyResult = printOrCopyService.printContent(content);
        futurePrintOrCopyResult.setPrintResult(realPrintOrCopyResult);
    }
}
