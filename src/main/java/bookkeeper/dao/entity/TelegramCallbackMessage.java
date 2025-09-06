package bookkeeper.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Setter
@Getter
@Entity
@Table(name = "telegram_callback_messages")
public class TelegramCallbackMessage {
    @Id
    private String key;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false, name = "created_at", updatable = false)
    private Instant createdAt;
}
