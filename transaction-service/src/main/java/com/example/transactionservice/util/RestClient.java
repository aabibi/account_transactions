package com.example.transactionservice.util;



import java.util.HashMap;
import java.util.Map;
import brave.Span;
import brave.Tracer;
import com.example.transactionservice.entity.model.AccountResponse;
import com.example.transactionservice.entity.model.TransactionRequest;
import com.example.transactionservice.entity.model.UpdateBalanceRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;


@Service
@RequiredArgsConstructor
@Transactional
public class RestClient {

    private static final String GET_account_ENDPOINT_URL = "http://ACCOUNT-SERVICE/accounts/{accountId}";
    private static final String POST_UPDATE_BALANCE_ENDPOINT_URL = "http://ACCOUNT-SERVICE/accounts/update_balance";


    private  Tracer tracer;


    private RestTemplate restTemplate;


    private WebClient.Builder webClientBuilder;


    public RestClient( RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AccountResponse getAccountById(long accountId) {


      //  Span inventoryServiceLookup = tracer.nextSpan().name("AccountServiceLookup");

      //  try (Tracer.SpanInScope isLookup = tracer.withSpanInScope(inventoryServiceLookup.start())) {

  //         inventoryServiceLookup.tag("call", "account-service");

            Map<String, Long> params = new HashMap<>();
            params.put("accountId", accountId);

            AccountResponse result = restTemplate.getForObject(GET_account_ENDPOINT_URL, AccountResponse.class, params);

            //  AccountResponse result = webClientBuilder.build().get().uri(GET_accounts_ENDPOINT_URL+ accountId)
            //         .retrieve().bodyToMono(AccountResponse.class).block();

            return result;
       /* }
        finally {
            inventoryServiceLookup.flush();
        }
*/


    }


    public AccountResponse updateAccountBalance(TransactionRequest transactionRequest) {


        UpdateBalanceRequest updateBalanceRequest  = new UpdateBalanceRequest();

        updateBalanceRequest.setAmountToUpdate(transactionRequest.getAmount());
        updateBalanceRequest.setAccountId(transactionRequest.getAccountId());
        updateBalanceRequest.setTransactionType(transactionRequest.getOperation_type());


        AccountResponse result = restTemplate.postForObject(POST_UPDATE_BALANCE_ENDPOINT_URL,updateBalanceRequest, AccountResponse.class);

        //  AccountResponse result = webClientBuilder.build().get().uri(GET_accounts_ENDPOINT_URL+ accountId)
        //         .retrieve().bodyToMono(AccountResponse.class).block();


        return result;

    }



/*
    private void createaccounts() {

        accounts newaccounts = new accounts("admin", "admin", "admin@gmail.com");

        RestTemplate restTemplate = new RestTemplate();
        accounts result = restTemplate.postForObject(CREATE_accounts_ENDPOINT_URL, newaccounts, accounts.class);

        System.out.println(result);
    }

    private void updateaccounts() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        accounts updatedaccounts = new accounts("admin123", "admin123", "admin123@gmail.com");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(UPDATE_accounts_ENDPOINT_URL, updatedaccounts, params);
    }

    private void deleteaccounts() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("id", "1");
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(DELETE_accounts_ENDPOINT_URL, params);
    }*/
}