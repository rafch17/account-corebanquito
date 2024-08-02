package com.banquito.core.account.controller;

import com.banquito.core.account.dto.AccountDTO;
import com.banquito.core.account.dto.ClientDTO;
import com.banquito.core.account.service.AccountService;
import com.banquito.core.account.util.mapper.AccountMapper;
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

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
    RequestMethod.PUT })
@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountMapper accountMapper;
    private final AccountService service;

    public AccountController(AccountMapper accountMapper, AccountService service) {
        this.accountMapper = accountMapper;
        this.service = service;
    }

    @GetMapping("/{codeInternalAccount}")
    public ResponseEntity<AccountDTO> getAccountByCodeUniqueAccount(@PathVariable String codeInternalAccount) {
        try {
            System.out.println("Va a buscar una cuenta por el numero:" + codeInternalAccount);
            return ResponseEntity.ok(this.accountMapper.toDTO(this.service.obtainAccount(codeInternalAccount)));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<AccountDTO> getAccountByClientId(@PathVariable String clientId) {
        try {
            System.out.println("Va a buscar una cuenta por id del cliente:" + clientId);
            return ResponseEntity.ok(this.accountMapper.toDTO(this.service.obtainAccountByClientId(clientId)));
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return ResponseEntity.notFound().build();

        }
    }

    @GetMapping("/client-by-account/{codeInternalAccount}")
    public ResponseEntity<ClientDTO> getClientByAccountId(@PathVariable String codeInternalAccount) {
        try {
            ClientDTO client = service.getClientByAccountId(codeInternalAccount);
            return ResponseEntity.ok(client);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        try {
            List<AccountDTO> accounts = this.service.obtainAllAccounts();
            return ResponseEntity.ok(accounts);
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping
    public ResponseEntity<AccountDTO> createOrUpdateAccount(@RequestBody AccountDTO dto) {
        try {
            AccountDTO dtoAC = this.service.create(dto);
            return new ResponseEntity<>(dtoAC, HttpStatus.CREATED);
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

}
