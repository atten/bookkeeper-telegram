package bookkeeper.telegram;

import bookkeeper.repositories.TelegramUserRepository;
import bookkeeper.services.parsers.BankingMessage;
import bookkeeper.services.parsers.BankingMessageParserRegistry;
import bookkeeper.services.parsers.TinkoffPurchaseSmsParser;
import bookkeeper.services.parsers.TinkoffPurchaseSmsWithDateParser;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Update;

import java.util.List;


/**
 * Parse SMS text from Tinkoff and categorize related expenses.
 */
public class TinkoffSmsHandler extends AbstractHandler {
    private final BankingMessageParserRegistry parserRegistry = new BankingMessageParserRegistry()
            .add(new TinkoffPurchaseSmsParser())
            .add(new TinkoffPurchaseSmsWithDateParser());

    TinkoffSmsHandler(TelegramBot bot, TelegramUserRepository telegramUserRepository) {
        super(bot, telegramUserRepository);
    }

    /**
     * Take Raw SMS, Transform to BalanceRecordDTO and put to BalanceRecordRepository.
     */
    @Override
    Boolean handle(Update update) {
        if (update.message() == null)
            return false;

        String[] smsList = update.message().text().split("\n");
        List<BankingMessage> result = parserRegistry.parseMultiple(smsList);
        if (result.size() != smsList.length)
            // at least one of provided sms was not parsed
            return false;
        logger.info(result.toString());
        return true;

    }


}
