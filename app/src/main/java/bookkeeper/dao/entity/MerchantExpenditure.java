package bookkeeper.dao.entity;

import bookkeeper.enums.Expenditure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Entity
@Table(name = "merchant_expenditures", uniqueConstraints = { @UniqueConstraint(columnNames = {"merchant", "expenditure", "telegram_user"}) })
public class MerchantExpenditure {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    @Setter
    private String merchant;

    @Column(nullable = false)
    @Setter
    private Expenditure expenditure;

    @Column(nullable = false)
    @Setter
    private int rank;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    @Setter
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at")
    @Setter
    private Instant createdAt;
}
