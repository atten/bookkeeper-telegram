package bookkeeper.telegram.scenarios.addTransfer;

import bookkeeper.entities.AccountTransfer;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

class AddTransferResponseFactory {
    private final AccountRepository accountRepository;

    AddTransferResponseFactory(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    String getDescriptionForWithdrawAccount() {
        return "Выберите счёт-источник:";
    }

    String getDescriptionForDepositAccount() {
        return "Выберите счёт-назначение:";
    }

    String getDescriptionForTransferCreated(AccountTransfer transfer) {
        return "Перевод создан!";
    }

    InlineKeyboardMarkup getKeyboardForWithdrawAccount(TelegramUser user, AddTransferCallback memory) {
        var kb = new InlineKeyboardMarkup();
        accountRepository.filter(user, memory.getWithdrawCurrency())
                .stream()
                .filter(account -> account.getId() != memory.getDepositAccountId())
                .map(account -> memory.setWithdrawAccountId(account.getId()).asButton(account.getName()))
                .forEach(kb::addRow);
        return kb;
    }

    InlineKeyboardMarkup getKeyboardForDepositAccount(TelegramUser user, AddTransferCallback memory) {
        var kb = new InlineKeyboardMarkup();
        accountRepository.filter(user, memory.getDepositCurrency())
                .stream()
                .filter(account -> account.getId() != memory.getWithdrawAccountId())
                .map(account -> memory.setDepositAccountId(account.getId()).asButton(account.getName()))
                .forEach(kb::addRow);
        return kb;
    }
}
