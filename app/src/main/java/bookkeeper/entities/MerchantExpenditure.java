package bookkeeper.entities;

import bookkeeper.enums.Expenditure;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "merchant_expenditures")
public class MerchantExpenditure {
    @Id
    @GeneratedValue
    @Getter
    private Long id;

    @Column(nullable = false)
    @Getter
    @Setter
    private String merchant;

    @Column(nullable = false)
    @Getter
    @Setter
    private Expenditure expenditure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    @Getter
    @Setter
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at")
    @Getter
    @Setter
    private Instant createdAt;
}
