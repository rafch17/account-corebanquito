package com.banquito.core.account.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClientDTO {
    String fullName;
    String email;
    String companyName;
}
