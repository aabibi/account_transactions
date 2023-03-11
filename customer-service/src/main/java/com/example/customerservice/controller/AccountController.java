package com.example.customerservice.controller;


import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.*;
import com.example.customerservice.exception.InssuficientBalanceUpdateException;
import com.example.customerservice.exception.InvalidNegativeBalanceUpdateException;
import com.example.customerservice.exception.UserAlreadyExistException;
import com.example.customerservice.exception.UserNotFoundException;
import com.example.customerservice.service.AccountService;
import com.example.customerservice.util.Utils;
import com.google.gson.Gson;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;



import java.math.BigDecimal;


@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation(value = "New Account", notes = "Add a new Account to the system.")
    @PostMapping
    public AccountResponse addAccount(@RequestBody AccountRequest accountRequest) throws UserAlreadyExistException {

        //validation
        if (!ObjectUtils.isEmpty(accountService.getUserByDocumentNumber(accountRequest.getDocument_number()))) {
            throw new UserAlreadyExistException("Account already exist. Aborting adding Account.");
        }

        Account newaccount = new Account();
        newaccount.setDocument_number(accountRequest.getDocument_number());
        newaccount.setAccountMoney(accountRequest.getInitalBalance());
        Account account = accountService.addAccount(newaccount);
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
    }

    @ApiOperation(value = "Get Account", notes = "Get account info based on account id in the system.")
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable long accountId) throws UserNotFoundException {

        Account account = accountService.getUserById(accountId);
        if (account == null) {
            throw new UserNotFoundException("Account with id " + accountId + " not found.");
        }
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());

    }


    @ApiOperation(value = "New Account", notes = "Add a new Account to the system.")
    @PostMapping("/update_balance")
    public AccountResponse updateAccountBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest)  {

        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
    }

}
