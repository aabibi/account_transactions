package com.example.customerservice;


import com.example.customerservice.entity.Account;
import com.example.customerservice.service.AccountService;

import org.checkerframework.checker.nullness.qual.AssertNonNullIfNonNull;
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
    private  AccountService accountService;


    @Test
    public void testGetAccountEndpoint() {

        Account user  = new Account();
        user.setDocument_number(45L);
        user.setAccountMoney(BigDecimal.valueOf(50.00));
        accountService.addAccount(user);

        Account account = restTemplate.getForObject(getRootUrl() + "/accounts/1", Account.class);
        Assertions.assertNotNull(account);
    }

    @Test
    public void testPostAccountEndpoint() {


        Account user  = new Account();
        user.setDocument_number(12345L);
        user.setAccountMoney(BigDecimal.valueOf(150.00));

        ResponseEntity<Account> postResponse = restTemplate.postForEntity(getRootUrl() + "/accounts", user, Account.class);
        Assertions.assertNotNull(postResponse);
        Assertions.assertNotNull(postResponse.getBody());
    }

    @Test
    void testAddandGetAccountByDocumentNumber()  {
        Account user  = new Account();
        user.setDocument_number(45L);
        user.setAccountMoney(BigDecimal.valueOf(50.00));
        accountService.addAccount(user);
        Assertions.assertNotNull(accountService.getUserByDocumentNumber(45L));
    }

    @Test
    void testGetUserFailed()  {
        Assertions.assertNotEquals(8L, accountService.getUserById(8L));
    }


    @Test
    void testUpdateAccountNegative()  {
        Account user  = new Account();
        user.setDocument_number(50L);
        user.setAccountMoney(BigDecimal.valueOf(150));
        accountService.addAccount(user);
        Assertions.assertEquals(BigDecimal.valueOf(200).doubleValue(), accountService.getUserByDocumentNumber(50L).getAccountMoney().add(BigDecimal.valueOf(50)).doubleValue());

    }

}
