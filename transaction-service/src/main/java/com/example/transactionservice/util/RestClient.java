package com.example.transactionservice.util;


import java.util.HashMap;
import java.util.Map;

import brave.Span;
import brave.Tracer;
import com.example.transactionservice.entity.model.AccountResponse;
import lombok.RequiredArgsConstructor;
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

    private Tracer tracer;


    public RestClient(RestTemplate restTemplate, Tracer tracer) {
        this.restTemplate = restTemplate;
        this.tracer = tracer;
    }

    public AccountResponse getAccountById(long accountId) throws Exception {


        Span inventoryServiceLookup = tracer.nextSpan().name("AccountServiceLookup");

        try (Tracer.SpanInScope ignored = tracer.withSpanInScope(inventoryServiceLookup.start())) {

            inventoryServiceLookup.tag("call", "account-service");

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

        } finally {
            inventoryServiceLookup.flush();
        }

    }


}