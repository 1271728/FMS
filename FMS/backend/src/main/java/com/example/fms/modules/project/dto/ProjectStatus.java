package com.example.fms.modules.project.dto;

public final class ProjectStatus {
    private ProjectStatus() {}

    public static final int DRAFT = 0;
    public static final int PENDING_UNIT = 1;
    public static final int PENDING_FINANCE = 2;
    public static final int APPROVED = 3;
    public static final int REJECTED = 4;
    public static final int CLOSED = 5;
    public static final int DISABLED = 6;
}
