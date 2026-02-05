package com.example.lunalash.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaction_record")
public class TransactionRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;

    @Column(name = "lash_artist", nullable = false, length = 10)
    private String lashArtist;

    @Column(name = "amount_before_discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountBeforeDiscount;

    @Column(name = "amount_after_discount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amountAfterDiscount;

    @Column(name = "discount_type", nullable = false, length = 20)
    private String discountType;

    @Column(name = "discount_rate", nullable = false, precision = 4, scale = 3)
    private BigDecimal discountRate;

    @Column(name = "payment_method", nullable = false, length = 20)
    private String paymentMethod;

    @Column(columnDefinition = "TEXT")
    private String remark;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
    
    /**
     * 操作項目
     */
    @OneToMany(
        mappedBy = "transaction",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OperationItemEntity> operationItems = new ArrayList<>();
    
    /**
     * 交易明細（實際金額、品項）
     */
    @OneToMany(
        mappedBy = "transaction",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<TransactionDetailEntity> transactionDetails = new ArrayList<>();

    // ===== Getter / Setter =====

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getLashArtist() {
        return lashArtist;
    }

    public void setLashArtist(String lashArtist) {
        this.lashArtist = lashArtist;
    }

    public BigDecimal getAmountBeforeDiscount() {
        return amountBeforeDiscount;
    }

    public void setAmountBeforeDiscount(BigDecimal amountBeforeDiscount) {
        this.amountBeforeDiscount = amountBeforeDiscount;
    }

    public BigDecimal getAmountAfterDiscount() {
        return amountAfterDiscount;
    }

    public void setAmountAfterDiscount(BigDecimal amountAfterDiscount) {
        this.amountAfterDiscount = amountAfterDiscount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public List<OperationItemEntity> getOperationItems() {
    	return operationItems;
    }
    
    public void setOperationItems(List<OperationItemEntity> operationItems) {
    	this.operationItems = operationItems;
    }
    
    public List<TransactionDetailEntity> getTransactionDetails() {
    	return transactionDetails;
    }
    
    public void setTransactionDetails(List<TransactionDetailEntity> transactionDetails) {
    	this.transactionDetails = transactionDetails;
    }
}
