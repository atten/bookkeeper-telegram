package bookkeeper.services.registries;

import bookkeeper.services.parsers.Spending;
import bookkeeper.services.parsers.SpendingParser;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * An aggregate of multiple parsers.
 * Only one parser is suitable for given message.
 */
public class SpendingParserRegistry {
    private final ArrayList<SpendingParser<? extends Spending>> spendingParsers = new ArrayList<>();

    public SpendingParserRegistry add(SpendingParser<? extends Spending> spendingParser) {
        spendingParsers.add(spendingParser);
        return this;
    }

    public Spending parse(String rawMessage) throws ParseException {
        ArrayList<Spending> candidates = new ArrayList<>();

        for (SpendingParser<? extends Spending> spendingParser : spendingParsers) {
            try {
                candidates.add(spendingParser.parse(rawMessage));
            } catch (ParseException e) {
                // will try another parser
            }
        }

        if (candidates.size() == 1)
            // single result as expected
            return candidates.get(0);

        if (candidates.size() == 0)
            throw new ParseException("No suitable SmsParser found.", 0);

        throw new ParseException("Multiple SmsParser found suitable.", 0);
    }

    public List<Spending> parseMultiple(String... rawMessages) {
        List<Spending> list = new ArrayList<>();
        for (String rawMessage : rawMessages) {
            Spending msg;
            try {
                msg = parse(rawMessage);
            } catch (ParseException e) {
                // skip sms if failed to parse
                continue;
            }
            list.add(msg);
        }
        return list;
    }
}
