package com.example.transactionservice.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.TransactionType;
import com.example.transactionservice.entity.model.AccountResponse;
import com.example.transactionservice.entity.model.TransactionMessage;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.exception.UserNotFoundException;
import com.example.transactionservice.util.RestClient;
import com.example.transactionservice.util.Utils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
        // Convert the message payload to a ResponseMessage object

        TransactionMessage transactionMessage = new Gson().fromJson(message, TransactionMessage.class);

        if (transactionMessage != null) {


            Transaction transaction = transactionService.getTransaction(transactionMessage.getTransactionId());
            //  Lets make sure this transaction has not already been updated ( checking the version )

            if (transaction != null &&  (Status.POSTED.getStatus_type() == transactionMessage.getTransactionStatus())) {
            //if (transaction != null) {

                    transaction.setTransactionStatus(transactionMessage.getTransactionStatus());
                    transaction.setEventDate(LocalDateTime.now());
                    transactionService.addTransaction(transaction);

            }
            else {
                    //lets cancelled this transaction
                    if (transaction != null) {
                        transactionService.updateTransactionStatus(transaction, Status.CANCELLED);
                    }
                    throw new Exception(transactionMessage.getErrorReason());
                }
        }




        // Pass the response to the transaction service for processing
     //   transactionService.addTransaction(null);
    }


}