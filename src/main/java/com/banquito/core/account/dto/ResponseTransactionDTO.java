package com.banquito.core.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResponseTransactionDTO {
    private String transactionType;
    private String creditorAccount;
    private String debitorAccount;

    private LocalDateTime createDate;
    private BigDecimal pendiente;
    private String status;
}
