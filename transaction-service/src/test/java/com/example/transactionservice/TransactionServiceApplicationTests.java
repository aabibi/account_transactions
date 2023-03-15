package com.example.transactionservice;

import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.Transaction;
import com.example.transactionservice.entity.TransactionType;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.service.TransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TransactionServiceApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionServiceApplicationTests {

  @Autowired
  private TestRestTemplate restTemplate;

  @LocalServerPort
  private int port;
  @Autowired
  private TransactionService transactionService;

  private String getRootUrl() {
    return "http://localhost:" + port;
  }




  @Test
  public void testGetTransactionEndpoint() {

    Transaction transaction  = new Transaction();
    transaction.setAccountID(1L);
    transaction.setTransactionType(TransactionType.INSTALLMENT_PURCHASE.getTransaction_type());
    transaction.setEventDate(LocalDateTime.now());
    transaction.setAmount(BigDecimal.valueOf(50.37));
    transaction.setTransactionStatus(Status.POSTED.getStatus_type());
    transactionService.addTransaction(transaction);

    Transaction account = restTemplate.getForObject(getRootUrl() + "/transactions/1", Transaction.class);
    Assertions.assertNotNull(account);
  }


  @Test
  public void testGetTransactionEndpointBadTransactionType() {

    TransactionRequest transactionRequest  = new TransactionRequest();
    transactionRequest.setOperation_type(9);
    transactionRequest.setAccountId(1);
    transactionRequest.setAmount(BigDecimal.valueOf(56.99));


    ResponseEntity<TransactionRequest> postResponse = restTemplate.postForEntity(getRootUrl() + "/transactions", transactionRequest, TransactionRequest.class);
    Assertions.assertNotNull(postResponse);
  }


  @Test
  public void testPostAccountEndpoint() {

    TransactionRequest transaction  = new TransactionRequest();
    transaction.setAccountId(1L);
    transaction.setOperation_type(TransactionType.INSTALLMENT_PURCHASE.getTransaction_type());
    transaction.setAmount(BigDecimal.valueOf(50.37));

    ResponseEntity<Transaction> postResponse = restTemplate.postForEntity(getRootUrl() + "/transactions", transaction, Transaction.class);
    Assertions.assertNotNull(postResponse);
    Assertions.assertNotNull(postResponse.getBody());
  }


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
