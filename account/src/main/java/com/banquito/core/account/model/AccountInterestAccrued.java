package com.banquito.core.account.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "ACCOUNT_INTEREST_ACCRUED")
public class AccountInterestAccrued implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ACCOUNT_INTEREST_ACCRUED_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ACCOUNT_ID", nullable = false)
    private Account account;

    @Column(name = "UNIQUE_KEY", length = 32, nullable = false)
    private String uniqueKey;

    @Column(name = "EXECUTION_DATE", nullable = false)
    private LocalDateTime executionDate;

    @Column(name = "AMMOUNT", precision = 17, scale = 2, nullable = false)
    private BigDecimal ammount;

    @Column(name = "INTEREST_RATE", precision = 5, scale = 2, nullable = false)
    private BigDecimal interestRate;

    @Column(name = "STATUS", length = 3, nullable = false)
    private String status;

    public AccountInterestAccrued(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AccountInterestAccrued other = (AccountInterestAccrued) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}