package com.example.customerservice.controller;


import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.AccountRequest;
import com.example.customerservice.entity.model.AccountResponse;
import com.example.customerservice.entity.model.TransactionType;
import com.example.customerservice.entity.model.UpdateBalanceRequest;
import com.example.customerservice.exception.InssuficientBalanceUpdateException;
import com.example.customerservice.exception.InvalidNegativeBalanceUpdateException;
import com.example.customerservice.exception.UserAlreadyExistException;
import com.example.customerservice.exception.UserNotFoundException;
import com.example.customerservice.service.AccountService;
import com.example.customerservice.util.Utils;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;



import java.math.BigDecimal;


@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("/accounts")
public class AccountController {


    private final int PURCHASE = 1;

    private AccountService accountService;

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
    public AccountResponse updateAccountBalance(@RequestBody UpdateBalanceRequest updateBalanceRequest) throws InvalidNegativeBalanceUpdateException, InssuficientBalanceUpdateException {


        validateUpdateBalanceRequest(updateBalanceRequest);
        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        return new AccountResponse(account.getAccountId(), account.getDocument_number(), account.getAccountMoney());
    }


    private void validateUpdateBalanceRequest(UpdateBalanceRequest updateBalanceRequest) throws InvalidNegativeBalanceUpdateException, InssuficientBalanceUpdateException {

        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        if (account != null) {

            TransactionType transactionType = Utils.validateTransactionType(updateBalanceRequest.getTransactionType());

            if (transactionType.name().equals(TransactionType.PAYMENT.name())) {

                if (updateBalanceRequest.getAmountToUpdate().doubleValue() > 0) {
                    accountService.creditMoney(account, updateBalanceRequest.getAmountToUpdate());
                } else {
                    throw new InvalidNegativeBalanceUpdateException("Payment transactions should also be greater than 0");
                }

            } else if (updateBalanceRequest.getAmountToUpdate().doubleValue() < 0 && (transactionType.name().equals(TransactionType.WITHDRAWAL.name()) || transactionType.name().equals(TransactionType.PURCHASE.name()) || transactionType.name().equals(TransactionType.INSTALLMENT_PURCHASE.name()))) {

                BigDecimal accountBalance = account.getAccountMoney().add(updateBalanceRequest.getAmountToUpdate());
                if (accountBalance.doubleValue() > 0) {
                    accountService.debitMoney(account, updateBalanceRequest.getAmountToUpdate());
                } else {
                    throw new InssuficientBalanceUpdateException("Transactions failed, not enough money to complete this.");
                }
            } else {
                throw new InvalidNegativeBalanceUpdateException("Purchase/installment purchase, withdrawal must be of negative amount");
            }


        }


    }

}
