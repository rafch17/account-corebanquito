package com.banquito.core.account.service;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.repository.AccountRepository;
import com.banquito.core.account.repository.AccountTransactionRepository;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class AccountTransactionService {
    private final AccountTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountTransactionMapper accountTransactionMapper;
    private final AccountService accountService;

    public AccountTransactionService(AccountTransactionRepository transactionRepository,
            AccountRepository accountRepository, AccountTransactionMapper accountTransactionMapper,
            AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountTransactionMapper = accountTransactionMapper;
        this.accountService = accountService;
    }

    public AccountTransaction processTransaction(AccountTransactionDTO dto) {
        if (Objects.nonNull(dto.getUniqueKey()) && Objects.nonNull(dto.getAccountId())
                && Objects.nonNull(dto.getCodeChannel()) && Objects.nonNull(dto.getAmount())) {
            Account debtor = accountService.obtainAccount(dto.getDebitorAccount());
            Account creditor = accountService.obtainAccount(dto.getCreditorAccount());
            AccountTransaction transaction1 = this.accountTransactionMapper.toPersistence(dto);
            boolean typeProced = debtor.getAvailableBalance().compareTo(transaction1.getAmount()) >= 0;
            if (debtor.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0) {
                transaction1.setCreateDate(LocalDateTime.now());
                transaction1.setBookingDate(LocalDateTime.now());
                transaction1.setValueDate(LocalDateTime.now());
                transaction1.setApplyTax(false);
                updateAccountBalance(debtor, creditor, transaction1, typeProced);
                return transactionRepository.save(transaction1);
            } else {
                throw new RuntimeException("Saldo insuficiente para realizar el cobro");
            }

        }
        throw new RuntimeException("Error en los datos de la transaccion");
    }

    private void updateAccountBalance(Account debtor, Account creditor, AccountTransaction transaction,
            boolean typeProced) {
        BigDecimal debCurrentBalance = debtor.getCurrentBalance();
        BigDecimal debAviableBalance = debtor.getAvailableBalance();
        BigDecimal credCurrentBalance = creditor.getCurrentBalance();
        BigDecimal credAviableBalance = creditor.getAvailableBalance();
        BigDecimal transactionammount = transaction.getAmount();

        if ("DEB".equals(transaction.getTransactionType())) {
            BigDecimal newDebitorCBalance = debCurrentBalance.subtract(transactionammount);
            BigDecimal newDebitorABalance = debAviableBalance.subtract(transactionammount);
            debtor.setCurrentBalance(newDebitorCBalance);
            debtor.setAvailableBalance(newDebitorABalance);
            BigDecimal newCreditorCBalance = credCurrentBalance.add(transactionammount);
            BigDecimal newCreditorABalance = credAviableBalance.add(transactionammount);
            creditor.setCurrentBalance(newCreditorCBalance);
            creditor.setAvailableBalance(newCreditorABalance);
            accountRepository.save(debtor);
            accountRepository.save(creditor);
            ;
        } else if ("CRE".equals(transaction.getTransactionType())) {
            BigDecimal newBalance = credCurrentBalance.add(transactionammount);
            BigDecimal newABalance = credAviableBalance.add(transactionammount);
            creditor.setCurrentBalance(newBalance);
            creditor.setAvailableBalance(newABalance);
            accountRepository.save(creditor);
        }
    }

    public List<AccountTransaction> findTransactionsByCodeUniqueAccount(String codeInternalAccount) {
        List<AccountTransaction> transactions = transactionRepository
                .findByAccount_CodeInternalAccount(codeInternalAccount);
        return transactions;
    }
}
