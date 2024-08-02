package com.banquito.core.account.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.model.DebitCard;
import com.banquito.core.account.repository.DebitCardRepository;

@Service
public class DebitCardService {
    private final DebitCardRepository repository;

    public DebitCardService(DebitCardRepository repository) {
        this.repository = repository;
    }

    public DebitCard obtainDebitCard(String cardNumber) {
        Optional<DebitCard> cardOpt = this.repository.findByCardNumber(cardNumber);
        if (cardOpt.isPresent()) {
            return cardOpt.get();
        } else {
            throw new RuntimeException("No existe la tarjeta con el numero" + cardNumber);
        }
    }

       public Account obtainAccountByPin(String pin){
        Optional<DebitCard> card = this.repository.findByPin(pin);
        if(card.isPresent()){
            return card.get().getAccount();
        }else{
            throw new RuntimeException("No existe la tarjeta con el PIN " + pin);
        }
    }
}
