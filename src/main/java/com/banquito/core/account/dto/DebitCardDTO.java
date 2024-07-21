package com.banquito.core.account.dto;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DebitCardDTO {
    private Integer id;
    private Integer clientId;
    private Integer accountId;
    private String cardNumber;
    private String ccv;
    private LocalDate expirationDate;
    private String cardholderName;
    private String cardUniqueKey;
    private String pin;
}
