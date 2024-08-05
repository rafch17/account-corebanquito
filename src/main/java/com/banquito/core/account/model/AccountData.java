package com.banquito.core.account.model;

import java.math.BigDecimal;

public class AccountData {
    private String creditorAccount;
    private BigDecimal amount;

    public AccountData(String creditorAccount, BigDecimal amount) {
        this.creditorAccount = creditorAccount;
        this.amount = amount;
    }

    public String getCreditorAccount() {
        return creditorAccount;
    }

    public void setCreditorAccount(String creditorAccount) {
        this.creditorAccount = creditorAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
