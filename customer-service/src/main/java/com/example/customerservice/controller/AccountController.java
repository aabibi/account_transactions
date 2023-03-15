package com.example.customerservice.controller;



import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.*;
import com.example.customerservice.exception.UserAlreadyExistException;
import com.example.customerservice.exception.UserNotFoundException;
import com.example.customerservice.service.AccountService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;


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

        if (!ObjectUtils.isEmpty(accountService.getUserByDocumentNumber(accountRequest.getDocument_number()))) {
            throw new UserAlreadyExistException("Account already exist. Aborting adding account.");
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


    @ApiOperation(value = "Update Account", notes = "Update account information in the system.")
    @PutMapping()
    public AccountResponse updateAccountBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest)  {

        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
    }

}
