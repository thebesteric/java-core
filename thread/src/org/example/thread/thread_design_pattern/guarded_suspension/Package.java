package org.example.thread.thread_design_pattern.guarded_suspension;

import lombok.Getter;

public final class Package {
    @Getter
    private final String name;

    public Package(String name) {
        this.name = name;
    }
}
