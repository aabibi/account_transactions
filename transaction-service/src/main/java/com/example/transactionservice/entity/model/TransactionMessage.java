package com.example.transactionservice.entity.model;

import com.example.transactionservice.entity.Transaction;

import java.math.BigDecimal;

public class TransactionMessage {


    private long transactionId;
    private long accountId;
    private int operation_type;
    private BigDecimal amount;
    private int status;

    private int  transactionStatus;

    private String comments;

    private long version;

    public TransactionMessage(Transaction transaction) {
        this.setTransactionId(transaction.getTransactionID());
        this.setTransactionStatus(transaction.getTransactionStatus());
        this.setStatus(transaction.getTransactionStatus());
        this.setAmount(transaction.getAmount());
        this.setOperation_type(transaction.getTransactionType());
        this.setAccountId(transaction.getAccountID());
        this.setVersion(transaction.getVersion());
    }

    public int getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(int transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(long transactionId) {
        this.transactionId = transactionId;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public long getAccountId() {
        return accountId;
    }

    public void setAccountId(long accountId) {
        this.accountId = accountId;
    }

    public int getOperation_type() {
        return operation_type;
    }

    public void setOperation_type(int operation_type) {
        this.operation_type = operation_type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
