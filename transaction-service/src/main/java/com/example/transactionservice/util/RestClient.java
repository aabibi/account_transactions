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


    private RestTemplate restTemplate;


    private WebClient.Builder webClientBuilder;


    public RestClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public AccountResponse getAccountById(long accountId) throws Exception {



            try {
                Map<String, Long> params = new HashMap<>();
                params.put("accountId", accountId);

                AccountResponse result = restTemplate.getForObject(GET_account_ENDPOINT_URL, AccountResponse.class, params);

                //  AccountResponse result = webClientBuilder.build().get().uri(GET_accounts_ENDPOINT_URL+ accountId)
                //         .retrieve().bodyToMono(AccountResponse.class).block();

                return result;
            } catch (Exception e) {

                throw new Exception(e.getMessage());

            }




    }


}