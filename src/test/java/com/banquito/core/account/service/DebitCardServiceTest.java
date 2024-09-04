package com.banquito.core.account.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import com.banquito.core.account.model.DebitCard;
import com.banquito.core.account.repository.DebitCardRepository;

public class DebitCardServiceTest {
    @Mock
    private DebitCardRepository repository;

    @InjectMocks
    private DebitCardService debitCardService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testObtainDebitCard_Found() {
        String cardNumber = "1234567890123456";
        DebitCard card = new DebitCard();
        card.setCardNumber(cardNumber);
        when(repository.findByCardNumber(cardNumber)).thenReturn(Optional.of(card));

        DebitCard result = debitCardService.obtainDebitCard(cardNumber);

        assertNotNull(result);
        assertEquals(cardNumber, result.getCardNumber());
        verify(repository, times(1)).findByCardNumber(cardNumber);
    }

    @Test
    void testObtainDebitCard_NotFound() {
        String cardNumber = "1234567890123456";
        when(repository.findByCardNumber(cardNumber)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            debitCardService.obtainDebitCard(cardNumber);
        });

        assertEquals("No existe la tarjeta con el numero" + cardNumber, exception.getMessage());
        verify(repository, times(1)).findByCardNumber(cardNumber);
    }
}
