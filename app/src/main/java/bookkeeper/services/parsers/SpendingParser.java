package bookkeeper.services.parsers;

import java.text.ParseException;

/**
 * Parser interface for given message type
 */
public interface SpendingParser<T extends Spending> {

    T parse(String rawMessage) throws ParseException;

}
