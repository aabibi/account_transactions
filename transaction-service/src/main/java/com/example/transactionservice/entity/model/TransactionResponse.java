package com.example.transactionservice.entity.model;

import com.example.transactionservice.entity.Transaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class TransactionResponse {

    private long transactionId;
    private long accountId;
    private int operation_type;
    private BigDecimal amount;

    private  BigDecimal balance;
    private String comment;

    private int  transactionStatus;

    public TransactionResponse(Transaction transaction) {

        this.accountId =transaction.getAccountID();
        this.amount = transaction.getAmount();
        this.operation_type = transaction.getTransactionType();
        this.transactionId  = transaction.getTransactionID();
        this.transactionStatus = transaction.getTransactionStatus();
        this.balance  = transaction.getBalance();
        this.comment = transaction.getComment();
    }

}
