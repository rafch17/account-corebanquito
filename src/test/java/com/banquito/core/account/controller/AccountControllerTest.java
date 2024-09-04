package com.banquito.core.account.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.banquito.core.account.dto.AccountDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.service.AccountService;
import com.banquito.core.account.util.mapper.AccountMapper;
import com.banquito.core.account.util.mapper.AccountMapperImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@WebMvcTest(AccountController.class)
@Import(AccountMapperImpl.class) 
public class AccountControllerTest {

    @MockBean
    private AccountService service;

    @MockBean
    private AccountMapper accountMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testGetAccountByClientId_Success() throws Exception {
        String clientId = "12345";
        Account account = new Account();
        account.setId(1L);
        account.setUniqueId("unique-id");
        account.setCodeProductType("type");
        account.setCodeProduct("product");
        account.setClientId(clientId);
        account.setCodeInternalAccount("internal-account");
        account.setCurrentBalance(BigDecimal.valueOf(1000.00));
        account.setAvailableBalance(BigDecimal.valueOf(800.00));
        account.setBlockedBalance(BigDecimal.valueOf(200.00));
        account.setCreationDate(LocalDateTime.now());

        AccountDTO accountDTO = AccountDTO.builder()
                .uniqueId("unique-id")
                .codeProductType("type")
                .codeProduct("product")
                .clientId(clientId)
                .codeInternalAccount("internal-account")
                .currentBalance(BigDecimal.valueOf(1000.00))
                .availableBalance(BigDecimal.valueOf(800.00))
                .blockedBalance(BigDecimal.valueOf(200.00))
                .build();

        when(service.obtainAccountByClientId(anyString())).thenReturn(account);
        when(accountMapper.toDTO(any(Account.class))).thenReturn(accountDTO);

        mockMvc.perform(get("/account-microservice/api/v1/accounts/client/12345"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uniqueId").value("unique-id"))
                .andExpect(jsonPath("$.codeProductType").value("type"))
                .andExpect(jsonPath("$.codeProduct").value("product"))
                .andExpect(jsonPath("$.clientId").value(clientId))
                .andExpect(jsonPath("$.codeInternalAccount").value("internal-account"))
                .andExpect(jsonPath("$.currentBalance").value(1000.00))
                .andExpect(jsonPath("$.availableBalance").value(800.00))
                .andExpect(jsonPath("$.blockedBalance").value(200.00));
    }

    @Test
    void testGetAccountByClientId_NotFound() throws Exception {
        String clientId = "12345";

        when(service.obtainAccountByClientId(anyString())).thenThrow(new RuntimeException("No existe la cuenta con el ID " + clientId));

        mockMvc.perform(get("/account-microservice/api/v1/accounts/client/12345"))
                .andExpect(status().isNotFound());
    }
}