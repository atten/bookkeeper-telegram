package bookkeeper.telegram.scenarios.save.tinkoff.parsers;

import bookkeeper.services.parsers.Spending;


/**
 * Example:
 * Никому не говорите код 1234! Вход в Тинькофф в 17:30 19.08.23
 */
public class TinkoffIgnoreSms implements Spending {

    @Override
    public String getMerchant() {
        return "Tinkoff";
    }


}
