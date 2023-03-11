package com.example.transactionservice.controller;


import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.model.TransactionMessage;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.entity.model.TransactionResponse;
import com.example.transactionservice.exception.TransactionFoundException;
import com.example.transactionservice.service.TransactionService;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private AmazonSQSAsync sqs;

    private TransactionService transactionService;

    private RestTemplate restTemplate;

    @Value("${sqs.account.url}")
    private String accountQueueUrl;


    @Value("${sqs.transaction.url}")
    private String transactionQueueUrl;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    public TransactionController(TransactionService transactionService, RestTemplate restTemplate) {
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
    }

    @ApiOperation(value = "Get Transaction information", notes = "Get Transaction info based on transaction id.")
    @GetMapping("/{transactionId}")
    public TransactionResponse getAccount(@PathVariable long transactionId) throws Exception {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            throw new TransactionFoundException("Transaction with id " + transactionId + " not found.");
        }
        return new TransactionResponse(transaction);

    }


    @ApiOperation(value = "Create Transaction information", notes = "Create a transaction info based on account id.")
    @PostMapping
    public TransactionResponse getAccount(@RequestBody TransactionRequest request) throws Exception {


        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(accountQueueUrl)
                .withMessageBody(new Gson().toJson(request));
        sqs.sendMessage(sendMsgRequest);



        String transacQueueUrl = sqs.getQueueUrl(transactionQueueUrl).getQueueUrl();
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(transacQueueUrl)
                .withMaxNumberOfMessages(1)
                .withWaitTimeSeconds(2); // wait for up to 2 seconds for a message
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        if (messages.size() > 0) {
            // process the message
            String messageBody = messages.get(0).getBody();
            TransactionMessage transactionResponse = new Gson().fromJson(messageBody, TransactionMessage.class);

            if (transactionResponse != null  && transactionResponse.getStatus() == 1) {
                Transaction transaction = transactionService.addTransaction(new Transaction(transactionResponse));
                sqs.deleteMessage(transacQueueUrl, messages.get(0).getReceiptHandle());
                return new TransactionResponse(transaction);
            }
            else {
                sqs.deleteMessage(transacQueueUrl, messages.get(0).getReceiptHandle());
                throw new TransactionFoundException("Transaction failed. Reason:" + transactionResponse.getErrorReason());
            }

        }

        return null;
    }


}
