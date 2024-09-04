package com.banquito.core.account.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.repository.AccountRepository;
import com.banquito.core.account.repository.AccountTransactionRepository;
import com.banquito.core.account.util.UniqueId.UniqueIdGeneration;
import com.banquito.core.account.util.UniqueId.UniqueKeyGeneration;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;

public class AccountTransactionServiceTest {
    @Mock
    private AccountTransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountTransactionMapper accountTransactionMapper;

    @Mock
    private AccountService accountService;

    @Mock
    private UniqueIdGeneration uniqueIdGeneration;

    @Mock
    private UniqueKeyGeneration uniqueKeyGeneration;

    @InjectMocks
    private AccountTransactionService accountTransactionService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessTransaction_Success() {
        // Arrange
        AccountTransactionDTO dto = AccountTransactionDTO.builder()
                .accountId(1)
                .codeChannel("channel")
                .amount(new BigDecimal("100.00"))
                .debitorAccount("2249598696") // ACCOUNTBANK
                .creditorAccount("2267578945") // ACCOUNTIVA
                .transactionType("TRANSFER")
                .reference("ref")
                .comission(new BigDecimal("10.00"))
                .build();

        Account debtor = new Account();
        debtor.setAvailableBalance(new BigDecimal("200.00"));
        Account creditor = new Account();

        when(accountService.obtainAccount("2249598696")).thenReturn(debtor);
        when(accountService.obtainAccount("2267578945")).thenReturn(creditor);
        when(accountTransactionMapper.toPersistence(dto)).thenReturn(new AccountTransaction());
        when(uniqueIdGeneration.generateUniqueId()).thenReturn("uniqueId");
        when(uniqueKeyGeneration.generateUniqueKey()).thenReturn("uniqueKey");

        // Act
        AccountTransactionDTO result = accountTransactionService.processTransaction(dto);

        // Assert
        assertNotNull(result);
        assertEquals("EXE", result.getStatus());
        assertEquals("2267578945", result.getCreditorAccount());
        assertEquals(dto.getAmount(), result.getAmount());
        assertEquals(dto.getComission(), result.getComission());
        assertNotNull(result.getCreateDate());
    }

    @Test
    public void testProcessTransaction_InsufficientFunds() {
        // Arrange
        AccountTransactionDTO dto = AccountTransactionDTO.builder()
                .accountId(1)
                .codeChannel("channel")
                .amount(new BigDecimal("100.00"))
                .debitorAccount("2249598696") // ACCOUNTBANK
                .creditorAccount("2267578945") // ACCOUNTIVA
                .transactionType("TRANSFER")
                .reference("ref")
                .comission(new BigDecimal("10.00"))
                .build();

        Account debtor = new Account();
        debtor.setAvailableBalance(new BigDecimal("10.00")); // Insufficient funds
        Account creditor = new Account();

        when(accountService.obtainAccount("2249598696")).thenReturn(debtor);
        when(accountService.obtainAccount("2267578945")).thenReturn(creditor);

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            accountTransactionService.processTransaction(dto);
        });
        assertEquals("Saldo insuficiente para realizar el cobro", thrown.getMessage());
    }

    @Test
    public void testProcessTransaction_InvalidData() {
        // Arrange
        AccountTransactionDTO dto = AccountTransactionDTO.builder()
                .amount(new BigDecimal("100.00"))
                // Missing required fields: accountId, codeChannel, debitorAccount, transactionType, reference
                .build();

        // Act & Assert
        RuntimeException thrown = assertThrows(RuntimeException.class, () -> {
            accountTransactionService.processTransaction(dto);
        });
        assertEquals("Error en los datos de la transaccion", thrown.getMessage());
    }
}

