package com.example.customerservice;


import com.example.customerservice.entity.Account;
import com.example.customerservice.service.AccountService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import java.math.BigDecimal;

@SpringBootTest
class CustomerServiceApplicationTests {

    @Autowired
    private  AccountService accountService;


    @Test
    void testGetAccountyDocumentNumber()  {
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
