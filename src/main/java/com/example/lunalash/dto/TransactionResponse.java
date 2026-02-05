package com.example.lunalash.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionResponse {
    private Long transactionId;
    private Long memberId;
    private LocalDateTime transactionTime;
    private String lashArtist;
    private BigDecimal amountBeforeDiscount;
    private BigDecimal amountAfterDiscount;
    private String discountType;
    private BigDecimal discountRate;
    private String paymentMethod;
    private String remark;

    // 關聯資料清單
    private List<OperationItemResponse> operationItems;
    private List<TransactionDetailResponse> transactionDetails;

    // --- Getter & Setter ---

    public Long getTransactionId() { return transactionId; }
    public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

    public Long getMemberId() { return memberId; }
    public void setMemberId(Long memberId) { this.memberId = memberId; }

    public LocalDateTime getTransactionTime() { return transactionTime; }
    public void setTransactionTime(LocalDateTime transactionTime) { this.transactionTime = transactionTime; }

    public String getLashArtist() { return lashArtist; }
    public void setLashArtist(String lashArtist) { this.lashArtist = lashArtist; }

    public BigDecimal getAmountBeforeDiscount() { return amountBeforeDiscount; }
    public void setAmountBeforeDiscount(BigDecimal amountBeforeDiscount) { this.amountBeforeDiscount = amountBeforeDiscount; }

    public BigDecimal getAmountAfterDiscount() { return amountAfterDiscount; }
    public void setAmountAfterDiscount(BigDecimal amountAfterDiscount) { this.amountAfterDiscount = amountAfterDiscount; }

    public String getDiscountType() { return discountType; }
    public void setDiscountType(String discountType) { this.discountType = discountType; }

    public BigDecimal getDiscountRate() { return discountRate; }
    public void setDiscountRate(BigDecimal discountRate) { this.discountRate = discountRate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }

    public List<OperationItemResponse> getOperationItems() { return operationItems; }
    public void setOperationItems(List<OperationItemResponse> operationItems) { this.operationItems = operationItems; }

    public List<TransactionDetailResponse> getTransactionDetails() { return transactionDetails; }
    public void setTransactionDetails(List<TransactionDetailResponse> transactionDetails) { this.transactionDetails = transactionDetails; }
}