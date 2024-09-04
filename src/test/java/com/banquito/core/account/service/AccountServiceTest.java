package com.banquito.core.account.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.banquito.core.account.dto.AccountDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.repository.AccountRepository;
import com.banquito.core.account.util.mapper.AccountMapper;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;

    @Mock
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        account = new Account();
        account.setCodeInternalAccount("2273445678");
        account.setClientId("JLK0043694");
        account.setState("ACT");
    }

    @Test
    void testObtainAccount_Found() {
        String codeInternalAccount = "2273445678";
        Account account = new Account();
        account.setCodeInternalAccount(codeInternalAccount);
        
        when(accountRepository.findByCodeInternalAccount(codeInternalAccount)).thenReturn(Optional.of(account));
        
        Account result = accountService.obtainAccount(codeInternalAccount);
        
        assertNotNull(result);
        assertEquals(codeInternalAccount, result.getCodeInternalAccount());
        verify(accountRepository, times(1)).findByCodeInternalAccount(codeInternalAccount);
    }

    @Test
    void testObtainAccount_NotFound() {
        String codeInternalAccount = "ACC123";

        when(accountRepository.findByCodeInternalAccount(codeInternalAccount)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.obtainAccount(codeInternalAccount);
        });

        assertEquals("No existe la cuenta con el numero" + codeInternalAccount, exception.getMessage());
    }

    @Test
    void testObtainAccountByClientId_Found() {
        String clientId = "JLK0043694";
        Account account = new Account();
        account.setClientId(clientId);
        
        when(accountRepository.findByClientId(clientId)).thenReturn(Optional.of(account));
        
        Account result = accountService.obtainAccountByClientId(clientId);
        
        assertNotNull(result);
        assertEquals(clientId, result.getClientId());
    }

    @Test
    void testObtainAccountByClientId_NotFound() {
        String clientId = "CLIENT123";
        
        when(accountRepository.findByClientId(clientId)).thenReturn(Optional.empty());
        
        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.obtainAccountByClientId(clientId);
        });

        String expectedMessage = "No existe la cuenta con el ID " + clientId;
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void testCreate_Success() {
        AccountDTO accountDTO = AccountDTO.builder()
            .codeInternalAccount("ACC123")
            .build();
    
        Account account = new Account();
        account.setCodeInternalAccount("ACC123");
    
        when(accountRepository.findByCodeInternalAccount("ACC123")).thenReturn(Optional.empty());
        when(accountMapper.toPersistence(accountDTO)).thenReturn(account);
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        when(accountMapper.toDTO(account)).thenReturn(accountDTO);
    
        AccountDTO result = accountService.create(accountDTO);
    
        assertNotNull(result);
        assertEquals("ACC123", result.getCodeInternalAccount());
    }

    @Test
    void testCreate_DuplicateCode() {
        AccountDTO accountDTO = AccountDTO.builder()
        .codeInternalAccount("ACC123")
        .build();

        Account account = new Account();
        account.setCodeInternalAccount("ACC123");

        when(accountRepository.findByCodeInternalAccount("ACC123")).thenReturn(Optional.of(account));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            accountService.create(accountDTO);
        });

        assertEquals("Código único repetido", exception.getMessage());
    }

    @Test
    void testSaveAccount_Success() {
        Account account = new Account();
        when(accountRepository.save(account)).thenReturn(account);

        Account result = accountService.saveAccount(account);

        assertNotNull(result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testObtainAllAccounts() {
        List<Account> accounts = List.of(new Account(), new Account());
    
        AccountDTO accountDTO1 = AccountDTO.builder()
            .codeInternalAccount("ACC001")
            .build();
        AccountDTO accountDTO2 = AccountDTO.builder()
            .codeInternalAccount("ACC002")
            .build();
    
        when(accountRepository.findAll()).thenReturn(accounts);
        when(accountMapper.toDTO(any(Account.class)))
            .thenReturn(accountDTO1, accountDTO2); 
    
        List<AccountDTO> result = accountService.obtainAllAccounts();
    
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(accountRepository, times(1)).findAll();
    }
}
