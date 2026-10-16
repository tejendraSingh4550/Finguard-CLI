package com.finguard.model;
import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
public record Transaction(Long id,String accountId,String reference,BigDecimal amount,String currency,TransactionType type,String merchant,LocalDateTime timestamp,TransactionStatus status,int riskScore,RiskLevel riskLevel,List<String> reasons) { public Transaction { reasons=List.copyOf(reasons); } }
