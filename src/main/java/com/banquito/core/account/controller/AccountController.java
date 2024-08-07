package com.banquito.core.account.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.account.dto.AccountDTO;
import com.banquito.core.account.dto.ClientDTO;
import com.banquito.core.account.service.AccountService;
import com.banquito.core.account.util.mapper.AccountMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/account-microservice/api/v1/accounts")
@Tag(name = "Account Controller", description = "API for managing accounts")
public class AccountController {

    private final AccountMapper accountMapper;
    private final AccountService service;

    public AccountController(AccountMapper accountMapper, AccountService service) {
        this.accountMapper = accountMapper;
        this.service = service;
    }

        @Operation(summary = "Get all accounts")
    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        try {
            List<AccountDTO> accounts = this.service.obtainAllAccounts();
            return ResponseEntity.ok(accounts);
        } catch (RuntimeException rte) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Create or update account")
    @PostMapping
    public ResponseEntity<AccountDTO> createOrUpdateAccount(@RequestBody AccountDTO dto) {
        try {
            AccountDTO dtoAC = this.service.create(dto);
            return new ResponseEntity<>(dtoAC, HttpStatus.CREATED);
        } catch (RuntimeException rte) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get account by codeInternalAccount")
    @GetMapping("/{codeInternalAccount}")
    public ResponseEntity<AccountDTO> getAccountByCodeUniqueAccount(@PathVariable String codeInternalAccount) {
        try {
            return ResponseEntity.ok(this.accountMapper.toDTO(this.service.obtainAccount(codeInternalAccount)));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get account by clientId")
    @GetMapping("/client/{clientId}")
    public ResponseEntity<AccountDTO> getAccountByClientId(@PathVariable String clientId) {
        try {
            return ResponseEntity.ok(this.accountMapper.toDTO(this.service.obtainAccountByClientId(clientId)));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get client by accountId")
    @GetMapping("/client-by-account/{codeInternalAccount}")
    public ResponseEntity<ClientDTO> getClientByAccountId(@PathVariable String codeInternalAccount) {
        try {
            ClientDTO client = service.getClientByAccountId(codeInternalAccount);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
