package com.banquito.core.account.service;

import com.banquito.core.account.dto.DebitCardDTO;
import com.banquito.core.account.model.DebitCard;
import com.banquito.core.account.repository.DebitCardRepository;
import com.banquito.core.account.util.mapper.DebitCardMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DebitCardService {

    private final DebitCardRepository debitCardRepository;
    private final DebitCardMapper debitCardMapper;

    public DebitCardService(DebitCardRepository debitCardRepository, DebitCardMapper debitCardMapper) {
        this.debitCardRepository = debitCardRepository;
        this.debitCardMapper = debitCardMapper;
    }

    @Transactional(readOnly = true)
    public List<DebitCardDTO> getAllDebitCards() {
        return debitCardRepository.findAll().stream()
                .map(debitCardMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DebitCardDTO getDebitCardById(Long id) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la tarjeta de débito con id: " + id));
        return debitCardMapper.toDTO(debitCard);
    }

    @Transactional
    public DebitCardDTO createDebitCard(DebitCardDTO debitCardDTO) {
        DebitCard debitCard = debitCardMapper.toPersistence(debitCardDTO);
        return debitCardMapper.toDTO(debitCardRepository.save(debitCard));
    }

    @Transactional
    public DebitCardDTO updateDebitCard(Long id, DebitCardDTO debitCardDTO) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la tarjeta de débito con id: " + id));
        debitCard.setCardNumber(debitCardDTO.getCardNumber());
        debitCard.setCcv(debitCardDTO.getCcv());
        debitCard.setExpirationDate(debitCardDTO.getExpirationDate());
        debitCard.setCardholderName(debitCardDTO.getCardholderName());
        debitCard.setCardUniqueKey(debitCardDTO.getCardUniqueKey());
        debitCard.setPin(debitCardDTO.getPin());
        return debitCardMapper.toDTO(debitCardRepository.save(debitCard));
    }

    @Transactional
    public void deleteDebitCard(Long id) {
        DebitCard debitCard = debitCardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe la tarjeta de débito con id: " + id));
        debitCardRepository.delete(debitCard);
    }
}
