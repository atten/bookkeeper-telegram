package bookkeeper.telegram.scenarios.addTransfer;

import bookkeeper.entities.AccountTransfer;
import bookkeeper.entities.TelegramUser;
import bookkeeper.services.repositories.AccountRepository;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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
        var dateVerbose = LocalDate.now().plusMonths(memory.getMonthOffset()).format(DateTimeFormatter.ofPattern("MMM yyyy"));
        return String.format("Выберите месяц перевода (выбран %s):", dateVerbose);
    }

    String getDescriptionForTransferCreated(AccountTransfer transfer) {
        var month = transfer.date().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
        return String.format("Перевод %s создан!", month);
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

    InlineKeyboardMarkup getKeyboardForMonth(AddTransferCallback memory) {
        var date = LocalDate.now();
        var offset = memory.getMonthOffset();
        return new InlineKeyboardMarkup().addRow(
            memory.setMonthOffset(offset - 1).asPrevMonthButton(date, offset - 1),
            memory.setMonthOffset(offset + 1).asNextMonthButton(date, offset + 1),
            memory.setMonthOffset(offset).markReady().asButton("✅ Готово")
        );
    }
}
