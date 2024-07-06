package com.banquito.core.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.banquito.core.account.model.AccountInterestAccrued;

public interface AccountInterestAccruedRepository extends JpaRepository<AccountInterestAccrued, Long> {

}
