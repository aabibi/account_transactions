package com.example.customerservice.controller;



import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.*;
import com.example.customerservice.exception.UserAlreadyExistException;
import com.example.customerservice.exception.UserNotFoundException;
import com.example.customerservice.service.AccountService;
import com.example.customerservice.util.Mapper;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {

    private AccountService accountService;

    Logger logger= LogManager.getLogger(AccountController.class);


    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @ApiOperation(value = "New Account", notes = "Add a new Account to the system.")
    @PostMapping
    public AccountResponse addAccount(@RequestBody AccountRequest accountRequest) throws UserAlreadyExistException {

        logger.info("AccountController:addAccount execution start");

        if (!ObjectUtils.isEmpty(accountService.getUserByDocumentNumber(accountRequest.getDocument_number()))) {
            throw new UserAlreadyExistException("Account already exist. Aborting adding account.");
        }

        Account newaccount = new Account();
        newaccount.setDocument_number(accountRequest.getDocument_number());

        //Balance is optional
        if (accountRequest.getInitalBalance() == null ) {
            newaccount.setAccountMoney(new BigDecimal("0.00"));
        }
        else {
            newaccount.setAccountMoney(accountRequest.getInitalBalance());
        }

        Account account = accountService.addAccount(newaccount);

        logger.info("AccountController:addAccount response  {} ", Mapper.mapToJsonString(account));
        logger.info("AccountController:addAccount execution ended.....");
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
    }

    @ApiOperation(value = "Get Account", notes = "Get account info based on account id in the system.")
    @GetMapping("/{accountId}")
    public AccountResponse getAccount(@PathVariable long accountId) throws UserNotFoundException {


        logger.info("AccountController:getAccount execution start");

        Account account = accountService.getUserById(accountId);
        if (account == null) {
            throw new UserNotFoundException("Account with id " + accountId + " not found.");
        }
        logger.info("AccountController:getAccount response  {} ", Mapper.mapToJsonString(account));
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());

    }



    @ApiOperation(value = "Get All Accounts", notes = "Get account info based on account id in the system.")
    @GetMapping()
    public List<Account> getAllAccount() throws UserNotFoundException {

        List<Account> accounts = accountService.getAllAccounts();
        if (accounts == null) {
            throw new UserNotFoundException("No accounts found.");
        }
        List<AccountResponse> accountResponses = new ArrayList<>();
        accounts.forEach( account -> accountResponses.add(new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney())));

        logger.info("AccountController:getAllAccount response  {} ", Mapper.mapToJsonString(accountResponses));
        return accounts;

    }

    @ApiOperation(value = "Update Account", notes = "Update account information in the system.")
    @PutMapping()
    public AccountResponse updateAccountBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest) throws UserNotFoundException {

        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        if (account != null) {
            account.setAccountMoney(updateBalanceRequest.getAmountToUpdate());
            accountService.addAccount(account);
            return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
        }

        throw new UserNotFoundException("Account with id " + updateBalanceRequest.getAccountId() + " not found.");
    }

}
