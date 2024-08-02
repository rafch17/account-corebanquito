package com.banquito.core.account.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountDTO {
    private String uniqueId;
    private String codeProductType;
    private String codeProduct;
    private String clientId;
    private String codeInternalAccount;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;

}
