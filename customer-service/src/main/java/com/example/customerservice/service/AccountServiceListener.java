package com.example.customerservice.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.TransactionMessage;
import com.example.customerservice.entity.model.TransactionType;
import com.example.customerservice.util.Utils;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountServiceListener {


    @Autowired
    private AmazonSQS sqs;

    @Value("${sqs.account.url}")
    private String accountQueueUrl;

    @Value("${sqs.transaction.url}")
    private String successQueueUrl;

    private AccountService accountService;

    public AccountServiceListener(AccountService accountService) {
        this.accountService = accountService;
    }

    @SqsListener(value = "${sqs.account.url}")
    public void receiveMessage(String message)   {

        TransactionMessage transaction = new Gson().fromJson(message, TransactionMessage.class);
        validateTransactionRequest(transaction);

    }

    private void validateTransactionRequest(TransactionMessage updateBalanceRequest)  {

        Account account = accountService.getUserById(updateBalanceRequest.getAccountId());
        SendMessageRequest sendMsgRequest = new SendMessageRequest();

        if (account != null) {

            TransactionType transactionType = Utils.validateTransactionType(updateBalanceRequest.getOperation_type());

            if ( transactionType == null ) {

                updateBalanceRequest.setTransactionStatus(3);
                updateBalanceRequest.setComments("Incorrect transaction Type. The valid range is between 1,2,3 and 4 only.");
                sendMsgRequest.withQueueUrl(successQueueUrl)
                        .withMessageBody(new Gson().toJson(updateBalanceRequest));
                sqs.sendMessage(sendMsgRequest);

            }

           else  if (transactionType.name().equals(TransactionType.PAYMENT.name())) {

                if (updateBalanceRequest.getAmount().doubleValue() > 0) {
                    accountService.creditMoney(account, updateBalanceRequest.getAmount());
                    updateBalanceRequest.setTransactionStatus(2);
                    sendMsgRequest.withQueueUrl(successQueueUrl)
                            .withMessageBody(new Gson().toJson(updateBalanceRequest));
                    sqs.sendMessage(sendMsgRequest);
                } else {
                    updateBalanceRequest.setTransactionStatus(3);
                    updateBalanceRequest.setComments("Payment transactions should also be greater than 0");
                    sendMsgRequest.withQueueUrl(successQueueUrl)
                            .withMessageBody(new Gson().toJson(updateBalanceRequest));
                    sqs.sendMessage(sendMsgRequest);
                }

            } else if (updateBalanceRequest.getAmount().doubleValue() < 0 && (transactionType.name().equals(TransactionType.WITHDRAWAL.name()) || transactionType.name().equals(TransactionType.PURCHASE.name()) || transactionType.name().equals(TransactionType.INSTALLMENT_PURCHASE.name()))) {

                BigDecimal accountBalance = account.getAccountMoney().add(updateBalanceRequest.getAmount());
                if (accountBalance.doubleValue() > 0) {
                    accountService.debitMoney(account, updateBalanceRequest.getAmount());
                    updateBalanceRequest.setTransactionStatus(2);
                    sendMsgRequest.withQueueUrl(successQueueUrl)
                            .withMessageBody(new Gson().toJson(updateBalanceRequest));
                    sqs.sendMessage(sendMsgRequest);
                } else {
                    //  throw new InssuficientBalanceUpdateException("Transactions failed, not enough money to complete this.");
                    updateBalanceRequest.setTransactionStatus(3);
                    updateBalanceRequest.setComments("Transactions failed, not enough money to complete this.");
                    sendMsgRequest.withQueueUrl(successQueueUrl)
                            .withMessageBody(new Gson().toJson(updateBalanceRequest));
                    sqs.sendMessage(sendMsgRequest);
                }
            } else {
                // throw new InvalidNegativeBalanceUpdateException("Purchase/installment purchase, withdrawal must be of negative amount");
                updateBalanceRequest.setTransactionStatus(3);
                updateBalanceRequest.setComments("Purchase/installment purchase, withdrawal must be of negative amount.");
                sendMsgRequest.withQueueUrl(successQueueUrl)
                        .withMessageBody(new Gson().toJson(updateBalanceRequest));
                sqs.sendMessage(sendMsgRequest);
            }


        } else {

            updateBalanceRequest.setTransactionStatus(3);
            updateBalanceRequest.setComments("Account not found.");
            sendMsgRequest.withQueueUrl(successQueueUrl)
                    .withMessageBody(new Gson().toJson(updateBalanceRequest));
            sqs.sendMessage(sendMsgRequest);

        }

    }
}
