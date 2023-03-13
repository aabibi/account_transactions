package com.example.transactionservice.service;


import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository  transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public Transaction getTransaction(long transactionID) {

        Optional<Transaction> transaction =  transactionRepository.findById(transactionID);
        return  transaction.orElse(null);
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Transaction addTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void deleteTransaction(Transaction transaction) {
         transactionRepository.delete(transaction);

    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateTransactionStatus(Transaction transaction, Status status) {
        transaction.setTransactionStatus(status.getStatus_type());
        transactionRepository.save(transaction);

    }

}
