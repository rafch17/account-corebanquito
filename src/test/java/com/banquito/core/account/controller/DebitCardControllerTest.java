package com.banquito.core.account.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.banquito.core.account.dto.DebitCardDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.model.DebitCard;
import com.banquito.core.account.service.DebitCardService;
import com.banquito.core.account.util.mapper.DebitCardMapper;

@WebMvcTest(DebitCardController.class)
public class DebitCardControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DebitCardService service;

    @MockBean
    private DebitCardMapper debitCardMapper;

    private DebitCard debitCard;
    private DebitCardDTO debitCardDTO;

    @BeforeEach
    void setUp() {
        debitCard = new DebitCard();
        debitCard.setId(1L);
        debitCard.setUniqueId("UNIQUE12345");
        debitCard.setClientId("CLIENT123");
        debitCard.setAccountId(1L);
        debitCard.setCardNumber("1234567890123456");
        debitCard.setCcv("123");
        debitCard.setExpirationDate(LocalDate.of(2025, 12, 31));
        debitCard.setCardholderName("John Doe");
        debitCard.setCardUniqueKey("UNIQUEKEY12345");
        debitCard.setPin("hashedPin123");

        debitCardDTO = DebitCardDTO.builder()
                .id(1)
                .clientId("CLIENT123")
                .accountId(1L)
                .cardNumber("1234567890123456")
                .ccv("123")
                .expirationDate(LocalDate.of(2025, 12, 31))
                .cardholderName("John Doe")
                .cardUniqueKey("UNIQUEKEY12345")
                .pin("hashedPin123")
                .build();
    }

    @Test
    void getAccountByPin_ShouldReturnAccount() throws Exception {
        Account account = new Account();
        account.setId(1L);
        account.setCodeInternalAccount("1234567890");

        given(service.obtainAccountByPin(anyString())).willReturn(account);

        mockMvc.perform(get("/account-microservice/api/v1/debit-cards/account-by-pin/{pin}", "hashedPin123")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.accountNumber").value("1234567890"));
    }

    @Test
    void getAccountByPin_ShouldReturnBadRequestOnException() throws Exception {
        given(service.obtainAccountByPin(anyString())).willThrow(new RuntimeException("No existe la tarjeta con el PIN"));

        mockMvc.perform(get("/account-microservice/api/v1/debit-cards/account-by-pin/{pin}", "invalidPin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getDebitCardByCardNumber_ShouldReturnDebitCardDTO() throws Exception {
        given(service.obtainDebitCard(anyString())).willReturn(debitCard);
        given(debitCardMapper.toDTO(debitCard)).willReturn(debitCardDTO);

        mockMvc.perform(get("/account-microservice/api/v1/debit-cards/by-card-number/{cardNumber}", "1234567890123456")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.cardNumber").value("1234567890123456"))
                .andExpect(jsonPath("$.cardholderName").value("John Doe"));
    }

    @Test
    void getDebitCardByCardNumber_ShouldReturnNotFoundOnException() throws Exception {
        given(service.obtainDebitCard(anyString())).willThrow(new RuntimeException("No existe la tarjeta con el numero"));

        mockMvc.perform(get("/account-microservice/api/v1/debit-cards/by-card-number/{cardNumber}", "invalidCardNumber")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
