package com.banquito.core.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountDTO {

    private Long id;
    private String codeProductType;
    private String codeProduct;
    private Long clientId;
    private String codeUniqueAccount;
    private String codeInternalAccount;
    private String codeInternationalAccount;
    private String status;
    private LocalDateTime creationDate;
    private LocalDateTime activationDate;
    private LocalDateTime lastModifiedDate;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;
    private LocalDateTime closedDate;
}