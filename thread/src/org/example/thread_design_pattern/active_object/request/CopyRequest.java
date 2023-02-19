package org.example.thread_design_pattern.active_object.request;

import org.example.thread_design_pattern.active_object.PrintOrCopyService;

public class CopyRequest extends PrintOrCopyRequest {

    private final String content;

    public CopyRequest(String content, PrintOrCopyService printOrCopyService) {
        super(printOrCopyService, null);
        this.content = content;
    }

    @Override
    public void execute() {
        printOrCopyService.copyContent(content);
    }
}
