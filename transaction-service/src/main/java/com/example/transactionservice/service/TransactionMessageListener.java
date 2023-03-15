package com.example.transactionservice.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.model.TransactionMessage;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;

@Service
public class TransactionMessageListener {


    @Autowired
    private AmazonSQS sqs;

    @Value("${sqs.account.url}")
    private String accountQueueUrl;

    @Value("${sqs.transaction.url}")
    private String successQueueUrl;

    private TransactionService transactionService;


    public TransactionMessageListener(TransactionService transactionService) {

        this.transactionService = transactionService;
    }

    @SqsListener(value = "${sqs.transaction.url}")
    public void receiveMessage(String message) throws Exception {

        TransactionMessage transactionMessage = new Gson().fromJson(message, TransactionMessage.class);
        if (transactionMessage != null) {

            Transaction transaction = transactionService.getTransaction(transactionMessage.getTransactionId());
            //  Lets make sure this transaction has not already been updated ( checking the version )
            // Handle concurrent transaction , if the version is not the same, that mean this record has already been updated (Optimistic lock)
            if (transaction != null && transaction.getVersion() != transactionMessage.getVersion()) {
                throw new OptimisticLockException("Transaction id: " +  transactionMessage.getTransactionId() +  " has already been updated.");
            }

            if (transaction != null &&  (Status.POSTED.getStatus_type() == transactionMessage.getTransactionStatus())) {

                    transaction.setTransactionStatus(transactionMessage.getTransactionStatus());
                    transaction.setEventDate(LocalDateTime.now());
                    transactionService.addTransaction(transaction);
                    transaction.setComment("Transaction Approved");
            }
            else {
                    if (transaction != null) {
                        transaction.setComment(transactionMessage.getComments());
                        transactionService.updateTransactionStatus(transaction, Status.CANCELLED);
                    }
                    throw new Exception(transactionMessage.getComments());
                }
        }




        // Pass the response to the transaction service for processing
     //   transactionService.addTransaction(null);
    }


}