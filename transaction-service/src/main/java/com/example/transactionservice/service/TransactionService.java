package com.example.transactionservice.service;


import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
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
        Transaction transaction1 =  transactionRepository.save(transaction);
        return  transaction1;

    }


}
