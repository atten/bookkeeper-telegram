package bookkeeper.entities;

import bookkeeper.enums.Expenditure;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "merchant_expenditures")
public class MerchantExpenditure {
    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false)
    private String merchant;

    @Column(nullable = false)
    private Expenditure expenditure;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    public Expenditure getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Expenditure expenditure) {
        this.expenditure = expenditure;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
