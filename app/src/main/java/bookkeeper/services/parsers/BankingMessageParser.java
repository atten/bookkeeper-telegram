package bookkeeper.services.parsers;

import java.text.ParseException;

/**
 * Parser interface for given message type
 */
public interface BankingMessageParser<T extends BankingMessage> {

    T parse(String rawMessage) throws ParseException;

}
