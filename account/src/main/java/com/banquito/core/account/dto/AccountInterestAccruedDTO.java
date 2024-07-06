package com.banquito.core.account.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.banquito.core.account.model.Account;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AccountInterestAccruedDTO {
    private Long id;
    private Account account;
    private String uniqueKey;
    private LocalDateTime executionDate;
    private BigDecimal ammount;
    private BigDecimal interestRate;
    private String status;
}
