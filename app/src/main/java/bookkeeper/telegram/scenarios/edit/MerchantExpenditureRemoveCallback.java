package bookkeeper.telegram.scenarios.edit;

import bookkeeper.enums.Expenditure;
import bookkeeper.telegram.shared.CallbackMessage;

import java.text.ParseException;
import java.util.Objects;

public class MerchantExpenditureRemoveCallback extends CallbackMessage {
    private static final String KEYWORD = "remove_association";
    private String merchant;
    private Expenditure expenditure;

    public MerchantExpenditureRemoveCallback() {}

    MerchantExpenditureRemoveCallback(String merchant, Expenditure expenditure) {
        this.merchant = merchant;
        this.expenditure = expenditure;
    }

    @Override
    public CallbackMessage parse(String message) throws ParseException {
        var parts = message.split("/");
        if (parts.length == 3 && Objects.equals(parts[0], KEYWORD)) {
            return new MerchantExpenditureRemoveCallback(parts[1], Expenditure.valueOf(parts[2]));
        }
        throw new ParseException("", 0);
    }

    String getMerchant() {
        return merchant;
    }

    Expenditure getExpenditure() {
        return expenditure;
    }

    public String toString() {
        return KEYWORD + "/" + merchant + "/" + expenditure;
    }
}
