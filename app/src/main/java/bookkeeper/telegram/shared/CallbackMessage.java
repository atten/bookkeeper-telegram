package bookkeeper.telegram.shared;

import com.pengrad.telegrambot.model.request.InlineKeyboardButton;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public abstract class CallbackMessage {
    public abstract CallbackMessage parse(String message) throws ParseException;

    public InlineKeyboardButton asButton(String text) {
        return new InlineKeyboardButton(text).callbackData(toString());
    }

    protected List<Long> parseIds(String string) {
        return Arrays.stream(string.split(",")).map(s -> {
            var pair = s.split("-");
            if (pair.length == 2) {
                return LongStream.rangeClosed(Long.parseLong(pair[0]), Long.parseLong(pair[1]));
            }
            return LongStream.of(Long.parseLong(s));
        })
            .reduce(LongStream::concat)
            .orElse(LongStream.empty())
            .boxed()
            .collect(Collectors.toList());
    }

    protected String idsToString(List<Long> ids) {
        var sortedIds = ids.stream().sorted().collect(Collectors.toList());
        List<String> result = new ArrayList<>();

        for (var value : sortedIds) {
            var item = value.toString();

            if (result.size() == 0) {
                result.add(item);
            } else if (result.size() == 1) {
                result.add("-");
                result.add(item);
            } else {
                var lastItem = result.get(result.size() - 1);

                if (Long.parseLong(lastItem) + 1 == value) {
                    var penultItem = result.get(result.size() - 2);
                    if (penultItem.equals("-")) {
                        result.set(result.size() - 1, item);
                    } else {
                        result.add("-");
                        result.add(item);
                    }
                } else {
                    result.add(",");
                    result.add(item);
                }
            }

        }

        return String.join("", result);
    }

}
