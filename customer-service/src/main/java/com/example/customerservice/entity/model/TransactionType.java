package com.example.customerservice.entity.model;

public enum TransactionType {
    PURCHASE(1),
    INSTALLMENT_PURCHASE(2),
    WITHDRAWAL(3),
    PAYMENT(4);

    private int transaction_type;
    TransactionType(int transaction_type) {
        this.transaction_type = transaction_type;
    }

    public int getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(int transaction_type) {
        this.transaction_type = transaction_type;
    }


}