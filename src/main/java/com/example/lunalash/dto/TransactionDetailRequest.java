package com.example.lunalash.dto;

import java.math.BigDecimal;

public class TransactionDetailRequest {

	private Long transactionId;
	private Long transactionDetailId;
	private String itemName;
	private BigDecimal itemPrice;
	private Short quantity;
	private String discountType;
	private BigDecimal discountRate;
	private BigDecimal discountPrice;
	
    public TransactionDetailRequest() {}
    
    public TransactionDetailRequest(Long transactionId, String itemName, BigDecimal itemPrice, Short quantity, String discountType, BigDecimal discountRate, BigDecimal discountPrice) {
        this.transactionId = transactionId;
    	this.itemName = itemName;
        this.itemPrice = itemPrice;
        this.quantity = quantity;
        this.discountType = discountType;
        this.discountRate = discountRate;
        this.discountPrice = discountPrice;
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
    public String getDiscountType() {
    	return discountType;
    }
    public BigDecimal getDiscountRate() {
    	return discountRate;
    }
    public BigDecimal getDiscountPrice() {
    	return discountPrice;
    }

}
