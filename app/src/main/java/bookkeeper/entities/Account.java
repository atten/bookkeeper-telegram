package bookkeeper.entities;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.Currency;

@Entity
@Table(name = "accounts", uniqueConstraints = { @UniqueConstraint(columnNames = { "name", "currency", "telegram_user" }) })
public class Account {
    @Id
    @GeneratedValue
    private long id;

    private String name;

    private String currency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "telegram_user")
    private TelegramUser telegramUser;

    @Column(nullable = false, name = "created_at")
    private Instant createdAt;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Currency getCurrency() {
        return Currency.getInstance(currency);
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.getCurrencyCode();
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

}
