package bookkeeper.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "telegram_users")
public class TelegramUser implements Serializable {
    @Id
    @Getter
    @Setter
    private long telegramId;

    @Column
    @Setter
    private String username;

    @Column(nullable = false, updatable = false)
    @Getter
    @Setter
    private Instant firstAccess;

    @Column(nullable = false)
    @Getter
    @Setter
    private Instant lastAccess;

    @Column(name = "language_code")
    @Getter
    @Setter
    private String languageCode;

    public String toString() {
        return String.format("%s (id=%d)", username, telegramId);
    }
}
