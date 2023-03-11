package com.example.transactionservice.service;


import com.amazonaws.services.sqs.AmazonSQS;
import com.example.transactionservice.entity.model.TransactionMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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

    //@SqsListener(value = "${sqs.transaction.url}")
    public void receiveMessage(String message)  {
        // Convert the message payload to a ResponseMessage object

        TransactionMessage transaction = new Gson().fromJson(message, TransactionMessage.class);

        // Pass the response to the transaction service for processing
        transactionService.addTransaction(null);
    }

}