package bookkeeper.telegram;

import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakeSession {
    private final User user;
    private final Bot bot;
    private final FakeTelegramBot fakeTelegramBot;
    private final LinkedList<Update> updates = new LinkedList<>();
    private final LinkedList<InlineKeyboardButton> buttons = new LinkedList<>();

    FakeSession(User user, Bot bot, FakeTelegramBot fakeTelegramBot) {
        this.user = user;
        this.bot = bot;
        this.fakeTelegramBot = fakeTelegramBot;
    }

    public FakeSession sendText(String input) {
        var update = new UpdateBuilder().setUser(user).setMessage(input).build();
        updates.add(update);
        bot.processUpdate(update);
        return this;
    }

    public FakeSession pressButton(String identifier) throws NoSuchElementException {
        var button = findButton(identifier).orElseThrow();
        var update = new UpdateBuilder().setUser(user).build();
        updates.add(update);
        bot.processUpdate(update);
        return this;
    }

    /**
     * Compare most recent bot answer with expected text
     */
    public FakeSession expect(String responseText) {
        var messages = fakeTelegramBot.getSentMessages();
        var lastMessage = messages.get(messages.size() - 1);
        var lastMessageText = (String) lastMessage.getParameters().getOrDefault("text", "");
        assertEquals(responseText, lastMessageText);
        return this;
    }

    /**
     * Compare most recent bot answer with expected text
     */
    public FakeSession expectStartsWith(String responseText) {
        var messages = fakeTelegramBot.getSentMessages();
        var lastMessage = messages.get(messages.size() - 1);
        var lastMessageText = (String) lastMessage.getParameters().getOrDefault("text", "");
        assertTrue(lastMessageText.startsWith(responseText), String.format("Expected message to start with: '%s': %s", responseText, lastMessageText));
        return this;
    }

    private Optional<InlineKeyboardButton> findButton(String identifier) {
        // iterate in reverse order (from latest to oldest)
        for (var iterator = buttons.descendingIterator(); iterator.hasNext(); ) {
            var button = iterator.next();
            if (button.text().contains(identifier)) {
                return Optional.of(button);
            }
        }
        return Optional.empty();
    }
}
