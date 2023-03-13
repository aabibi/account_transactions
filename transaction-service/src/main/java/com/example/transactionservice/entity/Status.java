package com.example.transactionservice.entity;

public enum Status {
    PENDING(1),
    POSTED(2),
    CANCELLED(3);

    private int status_type;

    Status(int status_type) {
        this.status_type = status_type;
    }

    public int getStatus_type() {
        return status_type;
    }

    public void setStatus_type(int status_type) {
        this.status_type = status_type;
    }
}