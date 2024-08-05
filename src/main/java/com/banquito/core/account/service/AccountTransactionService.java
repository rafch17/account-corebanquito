package com.banquito.core.account.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.banquito.core.account.dto.AccountTransactionDTO;
import com.banquito.core.account.model.Account;
import com.banquito.core.account.model.AccountTransaction;
import com.banquito.core.account.repository.AccountRepository;
import com.banquito.core.account.repository.AccountTransactionRepository;
import com.banquito.core.account.util.UniqueId.UniqueIdGeneration;
import com.banquito.core.account.util.UniqueId.UniqueKeyGeneration;
import com.banquito.core.account.util.mapper.AccountTransactionMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AccountTransactionService {
    private final AccountTransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountTransactionMapper accountTransactionMapper;
    private final AccountService accountService;
    private final UniqueIdGeneration uniqueIdGeneration;
    private final UniqueKeyGeneration uniqueKeyGeneration;

    private static final String ACCOUNTBANK = "2249598696";
    private static final String ACCOUNTIVA = "2267578945";
    private static final String ACCOUNTCOMISSION = "2286953637";
    private static final BigDecimal IVA = new BigDecimal("0.15");

    public AccountTransactionService(AccountTransactionRepository transactionRepository,
            AccountRepository accountRepository, AccountTransactionMapper accountTransactionMapper,
            AccountService accountService, UniqueIdGeneration uniqueIdGeneration,
            UniqueKeyGeneration uniqueKeyGeneration) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountTransactionMapper = accountTransactionMapper;
        this.accountService = accountService;
        this.uniqueIdGeneration = uniqueIdGeneration;
        this.uniqueKeyGeneration = uniqueKeyGeneration;
    }

    List<String> accountsRegister = new ArrayList<>();
    List<BigDecimal> amountRegister = new ArrayList<>();
    List<BigDecimal> pendientefinal = new ArrayList<>();

    public AccountTransactionDTO processTransaction(AccountTransactionDTO dto) {
        log.debug("Procesando transacción con DTO: {}", dto);
        if (Objects.nonNull(dto.getAccountId()) && Objects.nonNull(dto.getCodeChannel())
                && Objects.nonNull(dto.getAmount())
                && Objects.nonNull(dto.getDebitorAccount()) && Objects.nonNull(dto.getTransactionType())
                && Objects.nonNull(dto.getReference())) {
            BigDecimal valorIVA = dto.getComission().multiply(IVA);
            accountsRegister.add(dto.getCreditorAccount());
            amountRegister.add(dto.getAmount());
            log.info("DATOS EN LA LISTA ACCOUNT REGISTER {}", accountsRegister);
            log.info("DATOS EN LA LISTA AMOUNT REGISTER {}", amountRegister);

            Account debtor = accountService.obtainAccount(dto.getDebitorAccount());
            Account creditor = accountService.obtainAccount(dto.getCreditorAccount());

            AccountTransaction transaction1 = this.accountTransactionMapper.toPersistence(dto);

            if (dto.getDebitorAccount().equals(ACCOUNTBANK)) {
                creditor = accountService.obtainAccount(dto.getCreditorAccount());
                transaction1.setCreditorAccount(dto.getCreditorAccount());
            } else if (!dto.getCreditorAccount().equals(ACCOUNTBANK) &&
                    !dto.getCreditorAccount().equals(ACCOUNTIVA) &&
                    !dto.getCreditorAccount().equals(ACCOUNTCOMISSION)) {
                creditor = accountService.obtainAccount(ACCOUNTBANK);
                transaction1.setCreditorAccount(ACCOUNTBANK);

            } else if (dto.getCreditorAccount().equals(ACCOUNTCOMISSION)) {
                creditor = accountService.obtainAccount(ACCOUNTCOMISSION);
                transaction1.setCreditorAccount(ACCOUNTCOMISSION);

            } else if (dto.getCreditorAccount().equals(ACCOUNTIVA)) {
                creditor = accountService.obtainAccount(ACCOUNTIVA);
                transaction1.setCreditorAccount(ACCOUNTIVA);
            }

            // BigDecimal montoTotalRecaudo
            // =transaction1.getAmount().add(dto.getComission().add(valorIVA));
            BigDecimal recaudoMinimo = dto.getComission().add(valorIVA);
            BigDecimal pendiente;
            if (debtor.getAvailableBalance().compareTo(recaudoMinimo.add(BigDecimal.ONE)) > 0) {
                transaction1.setUniqueId(uniqueIdGeneration.generateUniqueId());
                transaction1.setUniqueKey(uniqueKeyGeneration.generateUniqueKey());
                transaction1.setTransactionSubtype("TRANSFER");
                transaction1.setCreateDate(LocalDateTime.now());
                transaction1.setBookingDate(LocalDateTime.now());
                transaction1.setValueDate(LocalDateTime.now());
                transaction1.setCreditorBankCode("BANCOD001");
                transaction1.setDebitorBankCode("BANCOD001");
                transaction1.setApplyTax(true);
                pendiente = updateAccountBalance(debtor, creditor, transaction1, recaudoMinimo.add(BigDecimal.ONE));
                transaction1.setAmount(transaction1.getAmount().subtract(pendiente));
                transaction1.setStatus("EXE");
                transactionRepository.save(transaction1);
                pendientefinal.add(pendiente);
                AccountTransactionDTO accountTransactionDTO1 = null;

                // Respuestas con orden
                if (transaction1.getCreditorAccount().equals(ACCOUNTBANK)) {
                    accountTransactionDTO1 = AccountTransactionDTO.builder()
                            .accountId(dto.getAccountId())
                            .codeChannel(dto.getCodeChannel())
                            .transactionType(transaction1.getTransactionType())
                            .reference(dto.getReference())
                            .createDate(transaction1.getCreateDate())
                            .status("EXE")
                            .comission(BigDecimal.ZERO)
                            .pendiente(pendiente)
                            .parentTransactionKey(transaction1.getUniqueId())
                            .amount(dto.getComission())
                            .creditorAccount(ACCOUNTCOMISSION)
                            .debitorAccount(dto.getDebitorAccount())
                            .build();
                    return processTransaction(accountTransactionDTO1);
                }
                if (transaction1.getCreditorAccount().equals(ACCOUNTCOMISSION)) {
                    Integer posicion = amountRegister.size();
                    BigDecimal valor = amountRegister.get(posicion-1);
                    AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                            .accountId(dto.getAccountId())
                            .codeChannel(dto.getCodeChannel())
                            .transactionType(transaction1.getTransactionType())
                            .reference(dto.getReference())
                            .createDate(transaction1.getCreateDate())
                            .status("EXE")
                            .comission(BigDecimal.ZERO)
                            .pendiente(pendiente)
                            .parentTransactionKey(transaction1.getUniqueId())
                            .amount(valor.multiply(new BigDecimal("0.15")))
                            .creditorAccount(ACCOUNTIVA)
                            .debitorAccount(dto.getDebitorAccount())
                            .build();
                    return processTransaction(accountTransactionDTO);
                }
                if (transaction1.getCreditorAccount().equals(ACCOUNTIVA)) {
                    log.info("SE PAPGO EL IVA Y LE VOY A PASAR EL ACREDITO ACOUNT {}", accountsRegister.get(0));
                    log.info("Y EL ARREGLO TIENE ESTO: {}", accountsRegister);
                    AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                            .accountId(dto.getAccountId())
                            .codeChannel(dto.getCodeChannel())
                            .transactionType(transaction1.getTransactionType())
                            .reference(dto.getReference())
                            .createDate(transaction1.getCreateDate())
                            .status("EXE")
                            .comission(BigDecimal.ZERO)
                            .pendiente(pendiente)
                            .parentTransactionKey(transaction1.getUniqueId())
                            .amount(amountRegister.get(0).subtract(pendientefinal.get(0)))
                            .creditorAccount(accountsRegister.get(0))
                            .debitorAccount(ACCOUNTBANK)
                            .build();
                    return processTransaction(accountTransactionDTO);
                }
                if (transaction1.getDebitorAccount().equals(ACCOUNTBANK)) {
                    log.info("SE HAN FINALIZADO LAS TRANSACCIONES CON EXITO");
                    AccountTransactionDTO accountTransactionDTO = AccountTransactionDTO.builder()
                            .accountId(dto.getAccountId())
                            .codeChannel(dto.getCodeChannel())
                            .transactionType(transaction1.getTransactionType())
                            .reference(dto.getReference())
                            .createDate(transaction1.getCreateDate())
                            .status("EXE")
                            .comission(BigDecimal.ZERO)
                            .pendiente(pendientefinal.get(0))
                            // .parentTransactionKey(transaction1.getUniqueId())
                            .amount(dto.getAmount())
                            .creditorAccount(dto.getCreditorAccount())
                            .debitorAccount(dto.getDebitorAccount())
                            .build();
                    // log.info("TAMANIO DE LISTA ACCOUNT {}",accountsRegister.size());
                    log.info("TAMANIO DE LISTA MONTOS {}", amountRegister.size());
                    return accountTransactionDTO;
                }
            } else {
                throw new RuntimeException("Saldo insuficiente para realizar el cobro");
            }
            accountsRegister.clear();
            amountRegister.clear();
            return null;
        }
        throw new RuntimeException("Error en los datos de la transaccion");
    }

    private BigDecimal updateAccountBalance(Account debtor, Account creditor, AccountTransaction transaction,
            BigDecimal recaudoMinimo) {
        BigDecimal debCurrentBalance = debtor.getCurrentBalance();
        BigDecimal debAviableBalance = debtor.getAvailableBalance();
        BigDecimal credCurrentBalance = creditor.getCurrentBalance();
        BigDecimal credAviableBalance = creditor.getAvailableBalance();
        BigDecimal transactionammount = transaction.getAmount();
        BigDecimal pendiente = BigDecimal.ZERO;

        if ("DEB".equals(transaction.getTransactionType())) {
            if (debAviableBalance.compareTo(transactionammount.add(recaudoMinimo)) >= 0) {
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
                BigDecimal saldoDisponible = debAviableBalance.subtract(recaudoMinimo);
                BigDecimal newDebitorCBalance = debCurrentBalance.subtract(saldoDisponible);
                BigDecimal newDebitorABalance = debAviableBalance.subtract(saldoDisponible);
                debtor.setCurrentBalance(newDebitorCBalance);
                debtor.setAvailableBalance(newDebitorABalance);
                pendiente = transactionammount.subtract(saldoDisponible);
                creditor.setCurrentBalance(credCurrentBalance.add(saldoDisponible));
                creditor.setAvailableBalance(credAviableBalance.add(saldoDisponible));
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
