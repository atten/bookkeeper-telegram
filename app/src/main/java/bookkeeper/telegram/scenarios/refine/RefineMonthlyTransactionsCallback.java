package bookkeeper.telegram.scenarios.refine;

import bookkeeper.telegram.shared.CallbackMessage;

import java.text.ParseException;

public class RefineMonthlyTransactionsCallback extends CallbackMessage {
    private static final String KEYWORD = "monthly_expenditures";

    public RefineMonthlyTransactionsCallback() {}

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var result = new RefineMonthlyTransactionsCallback();
        if (message.equals(KEYWORD))
            return result;
        throw new ParseException("", 0);
    }

    public String toString() {
        return KEYWORD;
    }
}
