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
    private int transactionType;

    @Column(name = "amount",nullable = false)
    private BigDecimal amount;

    @Column(name = "balance",nullable = false)
    private BigDecimal balance;



    @Column(name = "comment")
    private String comment;

    //optimistic lock to help against race condition
    @Version
    private long version;

    private LocalDateTime eventDate;


    @Column(name = "transaction_status",nullable = false)
    private int  transactionStatus;


    public Transaction(TransactionRequest request) {
        this.setAmount(request.getAmount());
        this.setTransactionType(Utils.validateTransactionType(request.getOperation_type()).getTransaction_type());
        this.setAccountID(request.getAccountId());
        this.setEventDate(LocalDateTime.now());
        this.setBalance(request.getAmount());
        this.setTransactionStatus(Status.PENDING.getStatus_type());
    }

    public Transaction(TransactionMessage responseMessage) {
        this.setAmount(responseMessage.getAmount());
        this.setTransactionType(Utils.validateTransactionType(responseMessage.getOperation_type()).getTransaction_type());
        this.setAccountID(responseMessage.getAccountId());
        this.setEventDate(LocalDateTime.now());
        this.setTransactionStatus(Utils.validateStatusType(responseMessage.getTransactionStatus()).getStatus_type());
    }
}
