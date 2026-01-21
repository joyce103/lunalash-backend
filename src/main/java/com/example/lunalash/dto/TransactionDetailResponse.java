package com.example.lunalash.dto;

import java.math.BigDecimal;

public class TransactionDetailResponse {

    private Long transactionDetailId;
    private String itemName;
    private BigDecimal itemPrice;
    private Short quantity;

    public TransactionDetailResponse(
            Long transactionDetailId,
            String itemName,
            BigDecimal itemPrice,
            Short quantity
    ) {
        this.transactionDetailId = transactionDetailId;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
    }

    // getters
}
