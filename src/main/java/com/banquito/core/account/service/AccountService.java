package com.banquito.core.account.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.banquito.core.account.dto.AccountDTO;
import com.banquito.core.account.dto.ClientDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.repository.AccountRepository;
import com.banquito.core.account.util.mapper.AccountMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountService {
    private final AccountRepository repository;
    private final AccountMapper accountMapper;

    public AccountService(AccountRepository repository, AccountMapper accountMapper) {
        this.repository = repository;
        this.accountMapper = accountMapper;
    }

    public Account obtainAccount(String codeInternalAccount) {
        Optional<Account> accountOpt = this.repository.findByCodeInternalAccount(codeInternalAccount);
        if (accountOpt.isPresent()) {
            return accountOpt.get();
        } else {
            throw new RuntimeException("No existe la ceunta con el numero" + codeInternalAccount);
        }
    }

    public Account obtainAccountByClientId(String clientId) {
        Optional<Account> accountOpt = this.repository.findByClientId(clientId);
        if (accountOpt.isPresent()) {
            return accountOpt.get();
        } else {
            throw new RuntimeException("No existe la cuenta con el ID " + clientId);
        }
    }

    public ClientDTO getClientByAccountId(String codeInternalAccount) {
        Account account = obtainAccount(codeInternalAccount);
        String uniqueId = account.getClientId();
        log.debug("Going to search client for account number: {}", codeInternalAccount);
        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:9090/api/v1")
                .build();

        ClientDTO dto = restClient.get()
                .uri("/client/{uniqueId}", uniqueId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(ClientDTO.class);

        if (dto != null) {
            log.info("Client found for account number: {}", codeInternalAccount);
            return dto;
        } else {
            throw new RuntimeException("Cliente no encontrado para el número de cuenta: " + codeInternalAccount);
        }
    }

    public AccountDTO create(AccountDTO dto) {

        Optional<Account> codeUnique = this.repository.findByCodeInternalAccount(dto.getCodeInternalAccount());
        if (codeUnique.isPresent()) {
            throw new RuntimeException("Código único repetido");
        }

        Account account = this.accountMapper.toPersistence(dto);
        account.setCreationDate(LocalDateTime.now());
        account.setLastModifiedDate(LocalDateTime.now());
        account.setState("INA");
        account.setCodeUniqueAccount(generateUniqueAccountCode());
        account.setCodeInternationalAccount(generateInternationalAccountCode());

        Account accountCreated = this.repository.save(account);
        return this.accountMapper.toDTO(accountCreated);
    }

    public Account saveAccount(Account account) {
        return repository.save(account);
    }

    public List<AccountDTO> obtainAllAccounts() {
        List<Account> accounts = this.repository.findAll();
        return accounts.stream().map(accountMapper::toDTO).collect(Collectors.toList());
    }

    private String generateUniqueAccountCode() {
        // Obtener el último valor de codeUniqueAccount y generar el nuevo valor
        String lastCodeUniqueAccount = repository.findTopByOrderByCodeUniqueAccountDesc()
                .map(Account::getCodeUniqueAccount)
                .orElse("CUA0000000");
        int newCode = Integer.parseInt(lastCodeUniqueAccount.substring(3)) + 1;
        return String.format("CUA%06d", newCode);
    }

    private String generateInternationalAccountCode() {
        // Obtener el último valor de codeInternationalAccount y generar el nuevo valor
        String lastCodeInternationalAccount = repository.findTopByOrderByCodeInternationalAccountDesc()
                .map(Account::getCodeInternationalAccount)
                .orElse("CIAINT0000000");
        int newCode = Integer.parseInt(lastCodeInternationalAccount.substring(6)) + 1;
        return String.format("CIAINT%06d", newCode);
    }
}