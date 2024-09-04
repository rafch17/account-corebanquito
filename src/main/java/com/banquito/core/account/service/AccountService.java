package com.banquito.core.account.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import java.security.SecureRandom;
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
            throw new RuntimeException("No existe la cuenta con el numero" + codeInternalAccount);
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
                .baseUrl("https://m4b60phktl.execute-api.us-east-1.amazonaws.com/banquito/client-microservice/api/v1/clients")
                .build();

        ClientDTO dto = restClient.get()
                .uri("/{uniqueId}", uniqueId)
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
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(1000000);
        return String.format("CUA%06d", randomNumber);
    }

    private String generateInternationalAccountCode() {
        SecureRandom random = new SecureRandom();
        int randomNumber = random.nextInt(1000000);
        String newCode = String.format("CIAINT%06d", randomNumber);

        return newCode;
    }
}