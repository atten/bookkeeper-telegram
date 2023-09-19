package bookkeeper.telegram.scenario.addTransfer;

import bookkeeper.service.repository.AccountRepository;
import bookkeeper.service.repository.AccountTransferRepository;
import bookkeeper.telegram.shared.AbstractHandler;
import bookkeeper.telegram.shared.Request;
import bookkeeper.telegram.shared.exception.AccountNotFound;
import bookkeeper.telegram.shared.exception.HandlerInterruptException;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Currency;
import java.util.List;
import java.util.Objects;

/**
 * Scenario: User adds new transfer.
 */
class AddTransferHandler implements AbstractHandler {
    private static final String COMMAND = "/new_transfer";

    private final AccountRepository accountRepository;
    private final AccountTransferRepository transferRepository;
    private final AddTransferResponseFactory responseFactory;

    @Inject
    AddTransferHandler(AccountRepository accountRepository, AccountTransferRepository transferRepository) {
        this.accountRepository = accountRepository;
        this.transferRepository = transferRepository;
        this.responseFactory = new AddTransferResponseFactory(accountRepository);
    }

    /**
     * Display help text (if sent without params), or create transfer interactively with given params.
     * Stage 0: display help text.
     * Stage 1: initial params, withdraw account selection.
     * Stage 2: deposit account selection.
     * Stage 3: month selection.
     * Stage 4: transfer creation.
     */
    public Boolean handle(Request request) throws HandlerInterruptException {
        // arrange sub-handlers in reverse order because latter stages contains more strict conditions.
        return createTransfer(request) ||
                displayMonthOffsetSelector(request) ||
                displayDepositAccountSelector(request) ||
                displayWithdrawAccountSelector(request) ||
                displayHelpMessage(request);
    }

    private Boolean displayHelpMessage(Request request) {
        var msg = request.getMessageText();
        if (!Objects.equals(msg, COMMAND))
            return false;

        var lines = List.of(
                String.format("Синтаксис: `%s [from_amount] [from_currency] [to_amount] [to_currency]`", COMMAND),
                String.format("Пример №1: `%s 1000 rub 10 usd`", COMMAND),
                String.format("Пример №2: `%s 1000 rub` (если суммы и валюты совпадают)", COMMAND)
        );

        request.sendMessage(String.join("\n", lines));
        return true;
    }

    private Boolean displayWithdrawAccountSelector(Request request) {
        var msg = request.getMessageText();
        if (!msg.startsWith(COMMAND))
            return false;

        var arguments = Arrays.stream(msg.split(" ")).skip(1).toList();

        BigDecimal amountWithdraw;
        Currency currencyWithdraw;
        BigDecimal amountDeposit;
        Currency currencyDeposit;

        try {
            if (arguments.size() == 2) {
                amountDeposit = new BigDecimal(arguments.get(0));
                currencyDeposit = Currency.getInstance(arguments.get(1).toUpperCase());
                amountWithdraw = amountDeposit.negate();
                currencyWithdraw = currencyDeposit;
            } else if (arguments.size() == 4) {
                amountWithdraw = new BigDecimal(arguments.get(0)).negate();
                currencyWithdraw = Currency.getInstance(arguments.get(1).toUpperCase());
                amountDeposit = new BigDecimal(arguments.get(2));
                currencyDeposit = Currency.getInstance(arguments.get(3).toUpperCase());
            } else {
                return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }

        var user = request.getTelegramUser();
        var memory = new AddTransferCallback(amountWithdraw, currencyWithdraw, amountDeposit, currencyDeposit);
        request.replyMessage(responseFactory.getDescriptionForWithdrawAccount(), responseFactory.getKeyboardForWithdrawAccount(user, memory));
        return true;
    }

    private Boolean displayDepositAccountSelector(Request request) {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof AddTransferCallback memory))
            return false;

        var user = request.getTelegramUser();
        request.editMessage(responseFactory.getDescriptionForDepositAccount(), responseFactory.getKeyboardForDepositAccount(user, memory));
        return true;
    }

    private Boolean displayMonthOffsetSelector(Request request) {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof AddTransferCallback memory))
            return false;

        // skip unless both accounts are selected
        if (memory.getWithdrawAccountId() == 0 || memory.getDepositAccountId() == 0)
            return false;

        request.editMessage(responseFactory.getDescriptionForMonth(memory), responseFactory.getKeyboardForMonth(memory));
        return true;
    }

    private Boolean createTransfer(Request request) throws AccountNotFound {
        var callbackMessage = request.getCallbackMessage();
        if (!(callbackMessage.isPresent() && callbackMessage.get() instanceof AddTransferCallback memory))
            return false;

        if (!memory.isReady())
            return false;

        var withdrawAccount = accountRepository.get(memory.getWithdrawAccountId()).orElseThrow(() -> new AccountNotFound(memory.getWithdrawAccountId()));
        var depositAccount = accountRepository.get(memory.getDepositAccountId()).orElseThrow(() -> new AccountNotFound(memory.getDepositAccountId()));
        var transfer = transferRepository.create(memory.getWithdrawAmount(), withdrawAccount, memory.getDepositAmount(), depositAccount, memory.getMonthOffset());
        request.editMessage(responseFactory.getDescriptionForTransferCreated(transfer));
        return true;
    }
}
