package com.example.transactionservice.controller;


import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.TransactionType;
import com.example.transactionservice.entity.model.AccountResponse;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.entity.model.TransactionResponse;
import com.example.transactionservice.exception.TransactionFoundException;
import com.example.transactionservice.exception.UserNotFoundException;
import com.example.transactionservice.service.TransactionService;
import com.example.transactionservice.util.RestClient;
import com.example.transactionservice.util.Utils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/transactions")
public class TransactionController {


    private TransactionService transactionService;

    private RestTemplate restTemplate;


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

        validateTransactionRequest(request);
        Transaction transaction = transactionService.addTransaction(new Transaction(request));
        return new TransactionResponse(transaction);

    }


    public void validateTransactionRequest(TransactionRequest transactionRequest) throws UserNotFoundException {

        RestClient restClient = new RestClient(restTemplate);

        TransactionType transactionType = Utils.validateTransactionType(transactionRequest.getOperation_type());
        if (transactionType == null) {
            throw new UserNotFoundException("Invalid transaction type");
        }

        AccountResponse account = restClient.getAccountById(transactionRequest.getAccountId());

        // first lets make sure the account owner exists
        if (account == null) {
            throw new UserNotFoundException("Account with id + " + transactionRequest.getAccountId() + " does not exist");
        }

        // validate the transaction (making sure we have enough money )
        restClient.updateAccountBalance(transactionRequest);

    }

}
