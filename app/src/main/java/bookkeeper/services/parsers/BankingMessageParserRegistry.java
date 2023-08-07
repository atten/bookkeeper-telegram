package bookkeeper.services.parsers;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * An aggregate of multiple parsers.
 * Only one parser is suitable for given message.
 */
public class BankingMessageParserRegistry {
    private final ArrayList<BankingMessageParser<? extends BankingMessage>> bankingMessageParsers = new ArrayList<>();

    public BankingMessageParserRegistry add(BankingMessageParser<? extends BankingMessage> bankingMessageParser) {
        bankingMessageParsers.add(bankingMessageParser);
        return this;
    }

    public BankingMessage parse(String rawMessage) throws ParseException {
        ArrayList<BankingMessage> candidates = new ArrayList<>();

        for (BankingMessageParser<? extends BankingMessage> bankingMessageParser : bankingMessageParsers) {
            try {
                candidates.add(bankingMessageParser.parse(rawMessage));
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

    public List<BankingMessage> parseMultiple(String... rawMessages) {
        List<BankingMessage> list = new ArrayList<>();
        for (String rawMessage : rawMessages) {
            BankingMessage msg;
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
