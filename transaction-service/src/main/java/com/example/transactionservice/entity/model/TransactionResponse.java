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
    private long account_Id;
    private int operation_type;
    private BigDecimal amount;

    public TransactionResponse(Transaction transaction) {

        this.account_Id =transaction.getAccountID();
        this.amount = transaction.getAmount();
        this.operation_type = transaction.getTransactionType();
        this.transactionId  = transaction.getTransactionID();
    }

    public TransactionResponse(TransactionMessage responseMessage) {

        this.account_Id =responseMessage.getAccountId();
        this.amount = responseMessage.getAmount();
        this.operation_type = responseMessage.getOperation_type();
    }
}
