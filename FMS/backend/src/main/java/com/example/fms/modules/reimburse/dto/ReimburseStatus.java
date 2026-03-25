package com.example.fms.modules.reimburse.dto;

public interface ReimburseStatus {
    int DRAFT = 0;
    int PENDING_UNIT = 1;
    int PENDING_FINANCE = 2;
    int WAIT_PAY = 3;
    int DONE = 4;
    int REJECTED = 5;
    int WITHDRAWN = 6;
    int PENDING_PI = 7;
}
