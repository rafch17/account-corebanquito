package com.banquito.core.account.controller;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.dto.ResponseTransactionDTO;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.service.AccountTransactionService;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST,
    RequestMethod.PUT })
@RestController
@RequestMapping("/api/v1/account-transactions")
public class AccountTransactionController {

    private AccountTransactionService transactionService;
    private final AccountTransactionMapper transactionMapper;

    public AccountTransactionController(AccountTransactionService transactionService,
            AccountTransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @PostMapping
    public ResponseEntity<ResponseTransactionDTO> createTransaction(@RequestBody AccountTransactionDTO dto) {
        try {
            System.out.println("DTO recibido: " + dto);
            ResponseTransactionDTO dtoTr = this.transactionService.processTransaction(dto);
            return ResponseEntity.ok(dtoTr);
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{codeInternalAccount}")
    public ResponseEntity<List<AccountTransactionDTO>> getTransactionsByCodeUniqueAccount(
            @PathVariable String codeInternalAccount) {
        List<AccountTransaction> transactions = transactionService
                .findTransactionsByCodeUniqueAccount(codeInternalAccount);
        List<AccountTransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }
}
