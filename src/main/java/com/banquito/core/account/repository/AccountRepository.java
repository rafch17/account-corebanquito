package com.banquito.core.account.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.banquito.core.account.model.Account;

public interface AccountRepository extends JpaRepository<Account,Integer>{
    Optional<Account> findByCodeInternalAccount(String codeInternalAccount);
    Optional<Account> findByClientId(String clientId);
    List<Account> findAll();
    Optional<Account> findTopByOrderByCodeUniqueAccountDesc();
    Optional<Account> findTopByOrderByCodeInternationalAccountDesc();
}
