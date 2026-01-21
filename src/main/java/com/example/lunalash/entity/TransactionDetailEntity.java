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

}
