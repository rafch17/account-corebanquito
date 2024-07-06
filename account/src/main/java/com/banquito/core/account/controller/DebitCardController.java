package com.banquito.core.account.controller;

import com.banquito.core.account.dto.DebitCardDTO;
import com.banquito.core.account.service.DebitCardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT,
        RequestMethod.DELETE })
@RestController
@RequestMapping("/debitcards")
public class DebitCardController {

    private final DebitCardService debitCardService;

    public DebitCardController(DebitCardService debitCardService) {
        this.debitCardService = debitCardService;
    }

    @GetMapping
    public ResponseEntity<List<DebitCardDTO>> getAllDebitCards() {
        List<DebitCardDTO> debitCards = debitCardService.getAllDebitCards();
        return ResponseEntity.ok(debitCards);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DebitCardDTO> getDebitCardById(@PathVariable Long id) {
        try {
            DebitCardDTO debitCard = debitCardService.getDebitCardById(id);
            return ResponseEntity.ok(debitCard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<DebitCardDTO> createDebitCard(@RequestBody DebitCardDTO debitCardDTO) {
        DebitCardDTO createdDebitCard = debitCardService.createDebitCard(debitCardDTO);
        return ResponseEntity.ok(createdDebitCard);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DebitCardDTO> updateDebitCard(@PathVariable Long id, @RequestBody DebitCardDTO debitCardDTO) {
        try {
            DebitCardDTO updatedDebitCard = debitCardService.updateDebitCard(id, debitCardDTO);
            return ResponseEntity.ok(updatedDebitCard);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDebitCard(@PathVariable Long id) {
        try {
            debitCardService.deleteDebitCard(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
