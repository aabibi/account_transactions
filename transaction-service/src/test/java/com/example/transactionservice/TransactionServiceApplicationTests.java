package com.example.transactionservice;

import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.TransactionType;
import com.example.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@SpringBootTest
class TransactionServiceApplicationTests {

  @Autowired
  private TransactionService transactionService;


  @Test
  void testCreateAndGetTransaction()  {

    Transaction transaction  = new Transaction();
    transaction.setAccountID(1L);
    transaction.setTransactionType(TransactionType.INSTALLMENT_PURCHASE.getTransaction_type());
    transaction.setEventDate(LocalDateTime.now());
    transaction.setAmount(BigDecimal.valueOf(50.37));
    transaction.setTransactionStatus(Status.POSTED.getStatus_type());
    transactionService.addTransaction(transaction);

    Assertions.assertEquals(1L, transactionService.getTransaction(1L).getTransactionID());

  }





}
