package com.example.transactionservice.controller;


import brave.Tracer;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.TransactionType;
import com.example.transactionservice.entity.model.TransactionMessage;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.entity.model.TransactionResponse;
import com.example.transactionservice.exception.InvalidTransactionTypeException;
import com.example.transactionservice.exception.InvalidTransactionException;
import com.example.transactionservice.exception.UserNotFoundException;
import com.example.transactionservice.service.TransactionService;
import com.example.transactionservice.util.RestClient;
import com.example.transactionservice.util.Utils;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.core.QueueMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;


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

    private Tracer tracer;

    @Autowired
    private QueueMessagingTemplate queueMessagingTemplate;

    public TransactionController(TransactionService transactionService, RestTemplate restTemplate,  Tracer tracer) {
        this.transactionService = transactionService;
        this.restTemplate = restTemplate;
        this.tracer = tracer;

    }

    @ApiOperation(value = "Get Transaction information", notes = "Get Transaction info based on transaction id.")
    @GetMapping("/{transactionId}")
    public TransactionResponse getAccount(@PathVariable long transactionId) throws Exception {

        Transaction transaction = transactionService.getTransaction(transactionId);
        if (transaction == null) {
            throw new InvalidTransactionException("Transaction with id " + transactionId + " not found.");
        }
        return new TransactionResponse(transaction);

    }


    @ApiOperation(value = "Create Transaction information", notes = "Create a transaction info based on account id.")
    @PostMapping
    public TransactionResponse getAccount(@RequestBody @Valid TransactionRequest request) throws Exception {


        validateTransactionRequest(request);
        Transaction transaction = transactionService.addTransaction(new Transaction(request));

        TransactionMessage transactionMessage = new TransactionMessage(transaction);
        SendMessageRequest sendMsgRequest = new SendMessageRequest()
                .withQueueUrl(accountQueueUrl)
                .withMessageBody(new Gson().toJson(transactionMessage));
        sqs.sendMessage(sendMsgRequest);

        return new TransactionResponse(transaction);

    }


    public void validateTransactionRequest(TransactionRequest transactionRequest) throws Exception {



        TransactionType transactionType = Utils.validateTransactionType(transactionRequest.getOperation_type());
        if ( transactionType == null ) {
            throw new InvalidTransactionTypeException("Incorrect transaction Type. The valid range is between 1,2,3 and 4 only.");
        }

        if (transactionRequest.getOperation_type() == 4 &&  transactionRequest.getAmount().doubleValue() < 0 ) {
            throw new InvalidTransactionException("Payment transactions should also be greater than 0");
        }

        if (transactionRequest.getOperation_type() != 4 &&  transactionRequest.getAmount().doubleValue() > 0 ) {
            throw new InvalidTransactionException("Purchase/installment purchase, withdrawal must be of negative amount.");
        }

        RestClient restClient = new RestClient(restTemplate, tracer);
        try {
             restClient.getAccountById(transactionRequest.getAccountId());
        }
        catch (Exception e) {
            throw new UserNotFoundException("Account with id: " + transactionRequest.getAccountId() + " does not exist.");
        }

    }
}
