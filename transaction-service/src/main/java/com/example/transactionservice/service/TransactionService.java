package com.example.transactionservice.service;

import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public List<Transaction> getTransactionByAccountId(long accountId) {

        return transactionRepository.findByAccountID(accountId);
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
