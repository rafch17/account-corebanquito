package com.banquito.core.account.dto;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class DebitCardDTO {
    private Long id;
    private Long accountId;
    private Integer clientId;
    private String cardNumber;
    private String ccv;
    private LocalDate expirationDate;
    private String cardholderName;
    private String cardUniqueKey;
    private String pin;
}
