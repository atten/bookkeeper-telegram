package bookkeeper.telegram;

import bookkeeper.service.telegram.KeyboardUtils;
import com.pengrad.telegrambot.model.User;
import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FakeSession {
    private final User user;
    private final Bot bot;
    private final FakeTelegramBot fakeTelegramBot;

    FakeSession(User user, Bot bot, FakeTelegramBot fakeTelegramBot) {
        this.user = user;
        this.bot = bot;
        this.fakeTelegramBot = fakeTelegramBot;
    }

    public FakeSession sendText(String input) {
        var update = new UpdateBuilder().setUser(user).setMessage(input).build();
        bot.processUpdate(update);
        return this;
    }

    public FakeSession pressButton(String identifier) throws NoSuchElementException {
        var button = findButton(identifier).orElseThrow(() -> new NoSuchElementException("Button '%s' not found".formatted(identifier)));
        var update = new UpdateBuilder().setUser(user).setCallbackQuery(button.callbackData()).build();
        bot.processUpdate(update);
        return this;
    }

    public FakeSession reply(String input) throws NoSuchElementException {
        var messages = fakeTelegramBot.getSentMessages();
        var lastMessage = messages.get(messages.size() - 1);
        var update = new UpdateBuilder()
            .setUser(user)
            .setMessage(input)
            .setReplyTo(lastMessage)
            .build();

        bot.processUpdate(update);
        return this;
    }

    /**
     * Compare most recent bot answer with expected text
     */
    public FakeSession expect(String responseText) {
        assertEquals(responseText, getLastResponseText());
        return this;
    }

    /**
     * Compare most recent bot answer with expected text
     */
    public FakeSession expectStartsWith(String responseText) {
        var lastMessageText = getLastResponseText();
        assertTrue(lastMessageText.startsWith(responseText), "Expected message to start with '%s': %s".formatted(responseText, lastMessageText));
        return this;
    }

    /**
     * Compare most recent bot answer with expected text
     */
    public FakeSession expectContains(String responseText) {
        var lastMessageText = getLastResponseTextNormalized();
        assertTrue(lastMessageText.contains(responseText), "Expect message to contain '%s': %s".formatted(responseText, lastMessageText));
        return this;
    }

    private String getLastResponseText() {
        var messages = fakeTelegramBot.getSentMessages();
        var lastMessage = messages.get(messages.size() - 1);
        //noinspection unchecked
        return (String) lastMessage.getParameters().getOrDefault("text", "");
    }

    /**
     * replace non-breaking spaces with regular ones in bot response
     */
    private String getLastResponseTextNormalized() {
        return getLastResponseText().replaceAll(Arrays.toString(Character.toChars(160)), " ");
    }

    private Optional<InlineKeyboardButton> findButton(String identifier) {
        // iterate in reverse order (from latest to oldest)
        for (var iterator = fakeTelegramBot.getSentMessages().descendingIterator(); iterator.hasNext(); ) {
            var message = iterator.next();

            if (!message.getParameters().containsKey("reply_markup")) {
                continue;
            }

            var keyboard = (InlineKeyboardMarkup) message.getParameters().get("reply_markup");
            var button = KeyboardUtils.getButtons(keyboard)
                .stream()
                .filter(b -> b.text().contains(identifier))
                .findFirst();

            if (button.isPresent())
                return button;
        }
        return Optional.empty();
    }
}
