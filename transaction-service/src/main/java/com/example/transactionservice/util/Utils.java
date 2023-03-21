package com.example.transactionservice.util;

import com.example.transactionservice.entity.Status;
import com.example.transactionservice.entity.TransactionType;

import java.util.HashMap;
import java.util.Map;

public class Utils {


    public static TransactionType validateTransactionType(int type) {

        return  integerTransactionTypeHashMap.get(type);
    }

    public static Status validateStatusType(int type) {

        return  integerStatusTypeHashMap.get(type);
    }


    private static Map<Integer, TransactionType> integerTransactionTypeHashMap = new HashMap<>();

    private static Map<Integer, Status> integerStatusTypeHashMap = new HashMap<>();


    static {
        for (TransactionType transactionType : TransactionType.values()) {
            integerTransactionTypeHashMap.put(
                    transactionType.getTransaction_type(),
                    transactionType
            );
        }


        for (Status status : Status.values()) {
            integerStatusTypeHashMap.put(
                    status.getStatus_type(),
                    status
            );
        }
    }
}
