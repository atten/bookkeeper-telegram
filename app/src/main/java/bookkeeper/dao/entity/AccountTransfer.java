package bookkeeper.dao.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Getter
@Entity
@Table(name = "account_transfers")
public class AccountTransfer {
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "withdraw_account_id")
    @Setter
    private Account withdrawAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "deposit_account_id")
    @Setter
    private Account depositAccount;

    @Column(nullable = false, name="withdraw_amount")
    @Setter
    private BigDecimal withdrawAmount;

    @Column(nullable = false, name="deposit_amount")
    @Setter
    private BigDecimal depositAmount;

    @Column(nullable = false, name = "created_at")
    @Setter
    private Instant createdAt;  // the moment when record added

    @Column(nullable = false)
    @Setter
    private Instant timestamp;  // the moment when transfer happened

    public LocalDate date() {
        return LocalDate.ofInstant(getTimestamp(), ZoneId.systemDefault());
    }
}
