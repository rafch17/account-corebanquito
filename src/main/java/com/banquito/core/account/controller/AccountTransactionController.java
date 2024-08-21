package com.banquito.core.account.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.service.AccountTransactionService;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/account-microservice/api/v1/account-transactions")
@Tag(name = "Account Transaction Controller", description = "API for managing account transactions")
public class AccountTransactionController {

    private final AccountTransactionService transactionService;
    private final AccountTransactionMapper transactionMapper;

    public AccountTransactionController(AccountTransactionService transactionService,
            AccountTransactionMapper transactionMapper) {
        this.transactionService = transactionService;
        this.transactionMapper = transactionMapper;
    }

    @Operation(summary = "Create a new transaction")
    @PostMapping
    public ResponseEntity<AccountTransactionDTO> createTransaction(@RequestBody AccountTransactionDTO dto) {
        try {
            System.out.println("DTO recibido: " + dto);
            AccountTransactionDTO dtoTr = this.transactionService.processTransaction(dto);
            return ResponseEntity.ok(dtoTr);
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Create a new Collection")
    @PostMapping("/collections")
    public ResponseEntity<AccountTransactionDTO>createCollection(@RequestBody AccountTransactionDTO dto){
        try{
            System.out.println("DTO recibido: "+dto);
            AccountTransactionDTO dtoTr = this.transactionService.processCollection(dto);
            return ResponseEntity.ok(dtoTr);
        } catch(RuntimeException rte){
            rte.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Get transactions by codeInternalAccount")
    @GetMapping("/{codeInternalAccount}")
    public ResponseEntity<List<AccountTransactionDTO>> getTransactionsByCodeUniqueAccount(
            @PathVariable String codeInternalAccount) {
        List<AccountTransaction> transactions = transactionService
                .findTransactionsByCodeUniqueAccount(codeInternalAccount);
        List<AccountTransactionDTO> transactionDTOs = transactionMapper.toDTOList(transactions);
        return ResponseEntity.ok(transactionDTOs);
    }
}
