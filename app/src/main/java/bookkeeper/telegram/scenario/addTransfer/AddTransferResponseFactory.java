package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.entity.AccountTransfer;
import bookkeeper.entity.TelegramUser;
import bookkeeper.service.repository.AccountRepository;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import static bookkeeper.service.telegram.StringUtils.*;

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

    String getDescriptionForMonth(AddTransferCallback memory) {
        return String.format("Выберите месяц перевода (выбран %s):", getMonthYearShort(memory.getMonthOffset()));
    }

    String getDescriptionForTransferCreated(AccountTransfer transfer) {
        return String.format(
            "Перевод %s ► %s (%s) создан!",
            transfer.getWithdrawAccount().getName(),
            transfer.getDepositAccount().getName(),
            getDateShort(transfer.date())
        );
    }

    InlineKeyboardMarkup getKeyboardForWithdrawAccount(TelegramUser user, AddTransferCallback memory) {
        var kb = new InlineKeyboardMarkup();
        accountRepository.filter(user, memory.getWithdrawCurrency())
                .stream()
                .filter(account -> account.isVisible() && account.getId() != memory.getDepositAccountId())
                .map(account -> memory.setWithdrawAccountId(account.getId()).asAccountButton(account))
                .forEach(kb::addRow);
        return kb;
    }

    InlineKeyboardMarkup getKeyboardForDepositAccount(TelegramUser user, AddTransferCallback memory) {
        var kb = new InlineKeyboardMarkup();
        accountRepository.filter(user, memory.getDepositCurrency())
                .stream()
                .filter(account -> account.isVisible() && account.getId() != memory.getWithdrawAccountId())
                .map(account -> memory.setDepositAccountId(account.getId()).asAccountButton(account))
                .forEach(kb::addRow);
        return kb;
    }

    InlineKeyboardMarkup getKeyboardForMonth(AddTransferCallback memory) {
        var offset = memory.getMonthOffset();
        return new InlineKeyboardMarkup().addRow(
            memory.setMonthOffset(offset - 1).asPrevMonthButton(offset - 1),
            memory.setMonthOffset(offset + 1).asNextMonthButton(offset + 1),
            memory.setMonthOffset(offset).markReady().asButton("✅ Готово")
        );
    }
}
