package com.example.lunalash.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransactionCreateRequest {

    public Long memberId;
    public LocalDateTime transactionTime;
    public String lashArtist;
    public BigDecimal amountBeforeDiscount;
    public BigDecimal amountAfterDiscount;
    public String discountType;
    public BigDecimal discountRate;
    public String paymentMethod;
    public String remark;

    public List<OperationItemRequest> operationItems;
    public List<TransactionDetailRequest> transactionDetails;
 }
