package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_detail")
public class TransactionDetailEntity {
	// 交易明細編號
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_detail_id")
    private Long transactionDetailId;
    
	// 交易單號
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private TransactionRecordEntity transaction;

    // 消費項目
    @Column(name = "item_name", nullable = false, length = 100)
    private String itemName;
    
    // 消費金額
    @Column(name = "item_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal itemPrice;
    
    // 消費數量
    @Column(name = "quantity", nullable = false)
    private Short quantity;
    
    // 消費折扣類型
    @Column(name = "discount_type", nullable = true, length = 30)
    private String discountType = "NONE"; 
    
    // 消費折扣（小數點）
    @Column(name = "discount_rate", nullable = true, precision = 4, scale = 3)
    private BigDecimal discountRate = BigDecimal.ONE;
    
    // 消費折扣（折錢）
    @Column(name = "discount_price", nullable = true, precision = 12, scale = 2)
    private BigDecimal discountPrice = BigDecimal.ZERO;
    
    // 折扣後價格
    @Column(name = "actual_price", precision = 12, scale = 2)
    private BigDecimal actualPrice;
    
    // ===== Getter / Setter =====

    public Long getTransactionDetailId() {
        return transactionDetailId;
    }

    public void setTransactionDetailId(Long transactionDetailId) {
        this.transactionDetailId = transactionDetailId;
    }
    
    public TransactionRecordEntity getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionRecordEntity transaction) {
        this.transaction = transaction;
    }
    
    public String getItemName () {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public BigDecimal getItemPrice () {
        return itemPrice;
    }

    public void setItemPrice(BigDecimal itemPrice) {
        this.itemPrice = itemPrice;
    }

    public Short getQuantity () {
        return quantity;
    }

    public void setQuantity(Short quantity) {
        this.quantity = quantity;
    }
    
    public String getDiscountType () {
    	return discountType;
    }
    
    public void setDiscountType (String discountType) {
    	this.discountType = discountType;
    }
    
    public BigDecimal getDiscountRate () {
    	return discountRate;
    }
    
    public void setDiscountRate (BigDecimal discountRate) {
    	this.discountRate = discountRate;
    }
    
    public BigDecimal getDiscountPrice () {
    	return discountPrice;
    }
    
    public void setDiscountPrice (BigDecimal discountPrice) {
    	this.discountPrice = discountPrice;
    }
    
    public BigDecimal getActualPrice() {
    	return actualPrice;
    }

    public void setActualPrice(BigDecimal actualPrice) {
    	this.actualPrice = actualPrice;
    }

}
