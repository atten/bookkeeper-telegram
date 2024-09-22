package bookkeeper.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;

@Setter
@Entity
@Table(name = "telegram_users")
public class TelegramUser implements Serializable {
    @Id
    @Getter
    private long telegramId;

    @Column
    private String username;

    @Column(nullable = false, updatable = false)
    @Getter
    private Instant firstAccess;

    @Column(nullable = false)
    @Getter
    private Instant lastAccess;

    @Column(nullable = false, name = "language_code")
    @Getter
    private String languageCode;

    public String toString() {
        return "%s (id=%d)".formatted(username, telegramId);
    }
}
