package com.example.customerservice.entity.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class UpdateBalanceRequest {


    private Long accountId;


    private BigDecimal amountToUpdate;


    private int  transactionType;


}
