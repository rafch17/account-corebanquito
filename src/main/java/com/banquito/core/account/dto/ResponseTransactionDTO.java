package com.banquito.core.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ResponseTransactionDTO {
    private Integer accountId;
    private String codeChannel;
    private BigDecimal amount;
    private String debitorAccount;
    private String creditorAccount;
    private String transactionType;
    private String reference;
    private LocalDateTime createDate;
    private String status;
    private BigDecimal comission;
    private BigDecimal pendiente;
    private String parentTransactionKey;
    private String accountCompany;
    private Boolean procesoTerminado;
}
