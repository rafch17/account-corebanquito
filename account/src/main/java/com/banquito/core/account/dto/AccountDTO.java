package com.banquito.core.account.dto;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountDTO {

    private Integer id;
    private Integer clientId;
    private String codeUniqueAccount;
    private String codeInternalAccount;
    private String codeInternationalAccount;
    private String number;
    private String status;
    private BigDecimal currentBalance;
    private BigDecimal availableBalance;
    private BigDecimal blockedBalance;
}