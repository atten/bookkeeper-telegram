package bookkeeper.entities;

import bookkeeper.enums.Expenditure;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;


@Entity
@Table(name = "account_transactions")
public class AccountTransaction {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Account account;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private Expenditure expenditure;

    @Column
    private String raw;

    @Column(name = "approved_at")
    private Instant approvedAt;  // the moment when transaction parameters have been approved manually (default is null)

    @Column(nullable = false)
    private Instant timestamp;  // the moment when transaction happened

    public boolean isEmpty() {
        return amount.equals(BigDecimal.ZERO);
    }

    public long getId() {
        return id;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Expenditure getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Expenditure expenditure) {
        this.expenditure = expenditure;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isApproved() {
        return approvedAt != null;
    }

    public void setApprovedAt(Instant approvedAt) {
        this.approvedAt = approvedAt;
    }

    public Duration age() {
        return Duration.between(getTimestamp(), Instant.now());
    }

    public LocalDate date() {
        return LocalDate.ofInstant(getTimestamp(), ZoneId.systemDefault());
    }
}
