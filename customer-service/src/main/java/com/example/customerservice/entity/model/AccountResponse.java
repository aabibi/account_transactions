package com.example.customerservice.entity.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AccountResponse implements Serializable {


    private long accountId;

    private long document_number;

    private BigDecimal balance;

}
