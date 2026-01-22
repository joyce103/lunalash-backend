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
    public Long getTransactionDetailId() {
        return transactionDetailId;
    }

    public String getItemName() {
        return itemName;
    }

    public BigDecimal getItemPrice() {
        return itemPrice;
    }

    public Short getQuantity() {
        return quantity;
    }

}
