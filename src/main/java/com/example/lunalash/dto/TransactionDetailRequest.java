package com.example.lunalash.dto;

import java.math.BigDecimal;

public class TransactionDetailRequest {

	private Long transactionId;
	private Long transactionDetailId;
	private String itemName;
	private BigDecimal itemPrice;
	private Short quantity;
	
    public TransactionDetailRequest(Long transactionId, String itemName, BigDecimal itemPrice, Short quantity) {
        this.transactionId = transactionId;
    	this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
    }
    
    public Long getTransactionDetailId() {
        return transactionDetailId;
    }
    
    public Long getTransactionId() {
    	return transactionId;
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
