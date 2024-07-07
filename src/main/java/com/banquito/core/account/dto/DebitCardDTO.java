package com.banquito.core.account.dto;

import java.time.LocalDate;
import com.banquito.core.account.model.Account;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class DebitCardDTO {
    private Long id;
    private Account account;
    private Long clientId;
    private String cardNumber;
    private String ccv;
    private LocalDate expirationDate;
    private String cardholderName;
    private String cardUniqueKey;
    private String pin;
}