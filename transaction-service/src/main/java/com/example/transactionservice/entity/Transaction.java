package com.example.transactionservice.entity;


import com.example.transactionservice.entity.model.TransactionMessage;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.util.Utils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id",nullable = false)
    private Long transactionID;

    @Column(name = "account_id",nullable = false)
    private Long accountID;

    @Column(name = "operation_type",nullable = false)
    private TransactionType transactionType;

    @Column(name = "amount",nullable = false)
    private BigDecimal amount;

    private LocalDateTime eventDate;

    public Transaction(TransactionRequest request) {
        this.setAmount(request.getAmount());
        this.setTransactionType(Utils.validateTransactionType(request.getOperation_type()));
        this.setAccountID(request.getAccountId());
        this.setEventDate(LocalDateTime.now());
    }

    public Transaction(TransactionMessage responseMessage) {
        this.setAmount(responseMessage.getAmount());
        this.setTransactionType(Utils.validateTransactionType(responseMessage.getOperation_type()));
        this.setAccountID(responseMessage.getAccountId());
        this.setEventDate(LocalDateTime.now());
    }
}
