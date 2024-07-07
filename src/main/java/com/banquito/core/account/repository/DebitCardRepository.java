package com.banquito.core.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banquito.core.account.model.DebitCard;

public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {

}
