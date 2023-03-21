package com.example.customerservice;


import com.example.customerservice.entity.Account;
import com.example.customerservice.entity.model.AccountRequest;
import com.example.customerservice.entity.model.AccountResponse;
import com.example.customerservice.entity.model.UpdateBalanceRequest;
import com.example.customerservice.service.AccountService;

import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CustomerServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerServiceApplicationTests {


    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String getRootUrl() {
        return "http://localhost:" + port;
    }

    @Autowired
    private AccountService accountService;


    @Before
    public void setup() {

        Account user = new Account();
        user.setDocument_number(12345L);
        user.setAccountMoney(BigDecimal.valueOf(50.00));
        accountService.addAccount(user);

        Account user2 = new Account();
        user2.setDocument_number(456789L);
        user2.setAccountMoney(BigDecimal.valueOf(50.00));
        accountService.addAccount(user2);

    }


    @Test
    public void testGetAccountEndpoint() {

        Account account = restTemplate.getForObject(getRootUrl() + "/accounts/1", Account.class);
        Assertions.assertNotNull(account);


    }

    @Test
    public void testPostAccountEndpoint() {

        AccountRequest user = new AccountRequest();
        user.setDocument_number(12345L);
        user.setInitalBalance(BigDecimal.valueOf(150.00));

        ResponseEntity<AccountResponse> postResponse = restTemplate.postForEntity(getRootUrl() + "/accounts", user, AccountResponse.class);
        Assertions.assertNotNull(postResponse);

        AccountResponse account = postResponse.getBody();
        Assertions.assertEquals(account.getDocument_number(), user.getDocument_number());
    }

    @Test
    void testGetAccountByDocumentNumberFailed() {

        Assertions.assertNull(accountService.getUserByDocumentNumber(0L));
    }


    @Test
    void testGetAccountByDocumentNumberSuccess() {

        Assertions.assertNotNull(accountService.getUserByDocumentNumber(12345L));
    }

    @Test
    void testGetUserFailed() {

        AccountResponse postResponse = restTemplate.getForObject(getRootUrl() + "/accounts/9999999", AccountResponse.class);
        Assertions.assertNull(postResponse.getBalance());


    }


    @Test
    void testUpdateAccountBalance() {

        Account user = new Account();
        user.setDocument_number(50L);
        user.setAccountMoney(BigDecimal.valueOf(150));
        accountService.addAccount(user);
        Account account = accountService.getUserByDocumentNumber(50L);
        account.setAccountMoney(account.getAccountMoney().add(new BigDecimal(250.99)));

        UpdateBalanceRequest updateBalanceRequest = new UpdateBalanceRequest();
        updateBalanceRequest.setAccountId(account.getAccountId());
        updateBalanceRequest.setAmountToUpdate(account.getAccountMoney());

        restTemplate.put(getRootUrl() + "/accounts", updateBalanceRequest);
        Assertions.assertEquals(BigDecimal.valueOf(400.99).doubleValue(), accountService.getUserByDocumentNumber(50L).getAccountMoney().doubleValue());

    }

}
