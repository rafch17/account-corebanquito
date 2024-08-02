package com.banquito.core.account.service;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.dto.ResponseTransactionDTO;
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

    public ResponseTransactionDTO processTransaction(AccountTransactionDTO dto) {
        if (Objects.nonNull(dto.getUniqueKey()) && Objects.nonNull(dto.getAccountId())
                && Objects.nonNull(dto.getCodeChannel()) && Objects.nonNull(dto.getAmount())) {
            Account debtor = accountService.obtainAccount(dto.getDebitorAccount());
            Account creditor = accountService.obtainAccount(dto.getCreditorAccount());
            AccountTransaction transaction1 = this.accountTransactionMapper.toPersistence(dto);
            boolean typeProced = debtor.getAvailableBalance().compareTo(transaction1.getAmount()) >= 0;
            BigDecimal pendiente;
            if (debtor.getAvailableBalance().compareTo(BigDecimal.ZERO) > 0) {
                transaction1.setCreateDate(LocalDateTime.now());
                transaction1.setBookingDate(LocalDateTime.now());
                transaction1.setValueDate(LocalDateTime.now());
                transaction1.setApplyTax(false);
                pendiente = updateAccountBalance(debtor, creditor, transaction1, typeProced);
                transaction1.setAmount(transaction1.getAmount().subtract(pendiente));
                transaction1.setStatus("EXE");
                transactionRepository.save(transaction1);

                return ResponseTransactionDTO.builder()
                        .transactionType(transaction1.getTransactionType())
                        .creditorAccount(transaction1.getCreditorAccount())
                        .debitorAccount(transaction1.getDebitorAccount())
                        .createDate(transaction1.getCreateDate())
                        .pendiente(pendiente)
                        .status("EXE")
                        .build();
            } else {
                throw new RuntimeException("Saldo insuficiente para realizar el cobro");
            }

        }
        throw new RuntimeException("Error en los datos de la transaccion");
    }

    private BigDecimal updateAccountBalance(Account debtor, Account creditor, AccountTransaction transaction,
            boolean typeProced) {
        BigDecimal debCurrentBalance = debtor.getCurrentBalance();
        BigDecimal debAviableBalance = debtor.getAvailableBalance();
        BigDecimal credCurrentBalance = creditor.getCurrentBalance();
        BigDecimal credAviableBalance = creditor.getAvailableBalance();
        BigDecimal transactionammount = transaction.getAmount();
        BigDecimal pendiente = BigDecimal.ZERO;

        if ("DEB".equals(transaction.getTransactionType())) {
            if (debAviableBalance.compareTo(transactionammount) > 0) {
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
                pendiente = BigDecimal.ZERO;
            } else {
                BigDecimal newDebitorCBalance = debCurrentBalance.subtract(debAviableBalance);
                debtor.setCurrentBalance(newDebitorCBalance);
                debtor.setAvailableBalance(BigDecimal.ZERO);
                pendiente = transactionammount.subtract(debAviableBalance);
                creditor.setCurrentBalance(credCurrentBalance.add(debAviableBalance));
                creditor.setAvailableBalance(credAviableBalance.add(debAviableBalance));
            }

        } else if ("CRE".equals(transaction.getTransactionType())) {
            BigDecimal newBalance = credCurrentBalance.add(transactionammount);
            BigDecimal newABalance = credAviableBalance.add(transactionammount);
            creditor.setCurrentBalance(newBalance);
            creditor.setAvailableBalance(newABalance);
            accountRepository.save(creditor);
            pendiente = BigDecimal.ZERO;
        }
        return pendiente;
    }

    public List<AccountTransaction> findTransactionsByCodeUniqueAccount(String codeInternalAccount) {
        List<AccountTransaction> transactions = transactionRepository
                .findByAccount_CodeInternalAccount(codeInternalAccount);
        return transactions;
    }
}
