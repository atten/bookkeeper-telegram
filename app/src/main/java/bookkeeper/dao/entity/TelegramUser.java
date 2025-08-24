package bookkeeper.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Setter
@Entity
@Table(name = "telegram_users")
public class TelegramUser implements Serializable {
    @Id
    @Getter
    private long telegramId;

    @Column
    private String username;

    @Column(nullable = false, updatable = false, name = "first_access")
    @Getter
    private LocalDate firstAccess;

    @Column(nullable = false, name = "last_access")
    @Getter
    private LocalDate lastAccess;

    @Column(nullable = false, name = "language_code")
    @Getter
    private String languageCode;

    public String toString() {
        return "%s (id=%d)".formatted(username, telegramId);
    }
}
