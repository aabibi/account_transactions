package com.example.customerservice.util;



import com.example.customerservice.entity.model.TransactionType;

import java.util.HashMap;
import java.util.Map;

public class Utils {


    public static TransactionType validateTransactionType(int type) {

//        TransactionType transactionType = null;
//        try {
//             transactionType = integerTransactionTypeHashMap.get(type);
//        } catch (Exception e) {
//
//        }
//
//        return transactionType;

        return  integerTransactionTypeHashMap.get(type);
    }

    private static Map<Integer, TransactionType> integerTransactionTypeHashMap = new HashMap<>();

    static {
        for (TransactionType transactionType : TransactionType.values()) {
            integerTransactionTypeHashMap.put(
                    transactionType.getTransaction_type(),
                    transactionType
            );
        }
    }
}
