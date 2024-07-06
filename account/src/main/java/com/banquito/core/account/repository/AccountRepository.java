package com.banquito.core.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banquito.core.account.model.Account;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
