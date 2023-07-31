package bookkeeper.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "telegram_users")
public class TelegramUser implements Serializable {
    @Id()
    private long telegramId;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private Instant firstAccess;

    @Column(nullable = false)
    private Instant lastAccess;

    public String toString() {
        return String.format("%s (id=%d)", username, telegramId);
    }

    public long getTelegramId() {
        return telegramId;
    }

    public void setTelegramId(long telegramId) {
        this.telegramId = telegramId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Instant getFirstAccess() {
        return firstAccess;
    }

    public void setFirstAccess(Instant firstAccess) {
        this.firstAccess = firstAccess;
    }

    public Instant getLastAccess() {
        return lastAccess;
    }

    public void setLastAccess(Instant lastAccess) {
        this.lastAccess = lastAccess;
    }
}
