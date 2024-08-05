package com.banquito.core.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.banquito.core.account.dto.DebitCardDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.service.DebitCardService;
import com.banquito.core.account.util.mapper.DebitCardMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT })
@RestController
@RequestMapping("/Account-Microservice/api/v1/debit-cards")
@Tag(name = "Debit Card Controller", description = "API for managing debit cards")
public class DebitCardController {

    private final DebitCardMapper debitCardMapper;
    private final DebitCardService service;

    public DebitCardController(DebitCardMapper debitCardMapper, DebitCardService service) {
        this.debitCardMapper = debitCardMapper;
        this.service = service;
    }

    @Operation(summary = "Get debit card by card number")
    @GetMapping("/by-card-number/{cardNumber}")
    public ResponseEntity<DebitCardDTO> getDebitCardByCardNumber(@PathVariable String cardNumber) {
        try {
            return ResponseEntity.ok(this.debitCardMapper.toDTO(this.service.obtainDebitCard(cardNumber)));
        } catch (RuntimeException rte) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Get account by PIN")
    @GetMapping("/account-by-pin/{pin}")
    public ResponseEntity<Account> getAccountByPin(@PathVariable String pin) {
        try {
            Account account = service.obtainAccountByPin(pin);
            return ResponseEntity.ok(account);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
