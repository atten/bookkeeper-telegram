package bookkeeper.service.telegram;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeyboardUtils {
    public static List<InlineKeyboardButton> getButtons(InlineKeyboardMarkup keyboard) {
        return Arrays.stream(keyboard.inlineKeyboard()).flatMap(Stream::of).toList();
    }

    public static InlineKeyboardMarkup createMarkupWithFixedColumns(List<InlineKeyboardButton> buttons, int columns) {
        AtomicInteger index = new AtomicInteger(0);
        var kb = new InlineKeyboardMarkup();
        // append keyboard rows
        buttons
            .stream()
            // split to N map items each contains a list of 'columns' buttons
            .collect(Collectors.groupingBy(i -> index.getAndIncrement() / columns))
            .values()
            .stream()
            .map(row -> row.toArray(InlineKeyboardButton[]::new))
            .forEach (kb::addRow);
        return kb;
    }

    static String getInlineKeyboardVerboseString(InlineKeyboardMarkup keyboard) {
        var joiner = new StringJoiner(", ");
        getButtons(keyboard).stream().map(InlineKeyboardButton::text).forEach(joiner::add);
        return "[%s]".formatted(joiner);
    }
}
