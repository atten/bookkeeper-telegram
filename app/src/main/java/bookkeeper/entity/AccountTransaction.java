package bookkeeper.entity;

import bookkeeper.enums.Expenditure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Getter
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    @Getter
    @Setter
    private Account account;

    @Column(nullable = false)
    @Getter
    @Setter
    private BigDecimal amount;

    @Column(nullable = false)
    @Getter
    @Setter
    private Expenditure expenditure;

    @Column
    @Getter
    @Setter
    private String raw;

    @Column(name = "created_at")
    @Getter
    @Setter
    private Instant createdAt;  // the moment when record added

    @Column(name = "approved_at")
    @Setter
    private Instant approvedAt;  // the moment when transaction parameters have been approved manually (default is null)

    @Column(nullable = false)
    @Getter
    @Setter
    private Instant timestamp;  // the moment when transaction happened

    public boolean isApproved() {
        return approvedAt != null;
    }

    public boolean isEmpty() {
        return amount.equals(BigDecimal.ZERO);
    }

    public Duration age() {
        return Duration.between(getTimestamp(), Instant.now());
    }

    public LocalDate date() {
        return LocalDate.ofInstant(getTimestamp(), ZoneId.systemDefault());
    }
}
